package com.viwath.srulibrarymobile.data.api

import android.util.Log
import com.viwath.srulibrarymobile.data.repository.TokenManager
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

//class AuthInterceptor @Inject constructor(
//    private val tokenManager: TokenManager,
//    private val authRepository: Lazy<AuthRepository>
//): Interceptor {
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//        val requestBuilder = originalRequest.newBuilder()
//
//        tokenManager.getAccessToken()?.let { token ->
//            requestBuilder.addHeader("Authorization", "Bearer $token")
//        }
//
//        var response = chain.proceed(requestBuilder.build())
//
//        if (response.code == 401){
//            //response.close()
//            synchronized(this){
//                val newAccessToken = runBlocking {
//                    authRepository.value.refreshToken()
//                }
//
//                if (newAccessToken){
//                    val newToken = tokenManager.getAccessToken() ?: return response
//                    val newRequest = requestBuilder
//                        .removeHeader("Authorization")
//                        .addHeader("Authorization", "Bearer $newToken")
//                        .build()
//                    response.close()
//                    response = chain.proceed(newRequest)
//                }
//            }
//
//        }
//        return response
//    }
//}

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: Lazy<AuthRepository>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Add the Authorization header if access token is available
        val accessToken = tokenManager.getAccessToken()
        val requestWithAuth = originalRequest.newBuilder().apply {
            accessToken?.let { addHeader("Authorization", "Bearer $it") }
        }.build()

        var response = chain.proceed(requestWithAuth)

        // Check for 401 Unauthorized response
        if (response.code == 401) {
            response.close() // Always close the previous response to avoid resource leaks

            synchronized(this) {
                val newAccessToken = runBlocking {
                    try {
                        val isTokenRefreshed = authRepository.value.refreshToken()
                        if (isTokenRefreshed) tokenManager.getAccessToken() else null
                    } catch (e: Exception) {
                        // Log and rethrow if token refresh fails
                        Log.e("AuthInterceptor", "Token refresh failed: ${e.message}", e)
                        null
                    }
                }

                if (newAccessToken != null) {
                    // Retry with the new token
                    val newRequest = originalRequest.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()

                    response = chain.proceed(newRequest)
                } else {
                    // Token refresh failed, return the original 401 response
                    Log.e("AuthInterceptor", "New access token is null, returning original response.")
                }
            }
        }

        return response
    }
}
