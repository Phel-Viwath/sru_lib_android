/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.api

import android.util.Log
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.AuthUseCase
import com.viwath.srulibrarymobile.utils.datastore.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * AuthInterceptor is an OkHttp interceptor responsible for managing authentication tokens.
 *
 * It handles the following:
 * 1. **Adding Authorization Header:**  Adds an "Authorization" header with a Bearer token to each outgoing request.
 * 2. **Token Refresh:** If a request returns a 401 (Unauthorized) error, it attempts to refresh the access token.
 * 3. **Retry:** If the token is successfully refreshed, it retries the original request with the new token.
 * 4. **Error Handling:** Throws an `IllegalStateException` if the token refresh succeeds but no new token is found.
 *
 * @property userPreferences Manages the storage and retrieval of authentication tokens.
 * @property authUseCase A lazy-loaded instance of the AuthUseCase, used for refreshing the token.
 */
class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authUseCase: Lazy<AuthUseCase>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("AuthInterceptor", "intercept: Called")
        return runBlocking {
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()

            // Try with existing token first
            userPreferences.getAccessToken()?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            val response = chain.proceed(requestBuilder.build())

            // Handle 401 by refreshing token and retrying once
            if (response.code == 401) {
                response.close()

                // Refresh token synchronously
                val newAccessToken = authUseCase.value.refreshTokenUseCase()
                Log.d("AuthInterceptor", "access token: $newAccessToken")

                if (newAccessToken) {
                    val newToken = userPreferences.getAccessToken()
                        ?: throw IllegalStateException("Token refresh succeeded but no token found")

                    // Create new request with new token
                    val newRequest = originalRequest.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newToken")
                        .build()

                    // Return new response
                    return@runBlocking chain.proceed(newRequest)
                }
            }

            response
        }
    }
}

//class AuthInterceptor @Inject constructor(
//    private val tokenManager: TokenManager, // Inject TokenManager (TokenManager use to store and get token)
//    private val authUseCase: Lazy<AuthUseCase>
//): Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//        val requestBuilder = originalRequest.newBuilder()
//        tokenManager.getAccessToken()?.let { token ->
//            requestBuilder.addHeader("Authorization", "Bearer $token")
//        }
//        var response = chain.proceed(requestBuilder.build())
//        if (response.code == 401){
//            response.close()
//            synchronized(this){
//                GlobalScope.launch {
//                    val newAccessToken = authUseCase.value.refreshTokenUseCase()
//                    Log.d("AuthInterceptor", "access token: $newAccessToken")
//                    if (newAccessToken){
//                        val newToken = tokenManager.getAccessToken() ?: return@launch
//                        val newRequest = requestBuilder
//                            .removeHeader("Authorization")
//                            .addHeader("Authorization", "Bearer $newToken")
//                            .build()
//                        response = chain.proceed(newRequest)
//                    }
//                }
//            }
//        }
//        return response
//    }
//}

//class AuthInterceptor @Inject constructor(
//    private val tokenManager: TokenManager,
//    private val authUseCase: Lazy<AuthUseCase>
//): Interceptor {
//
//    private var isRefreshing = false  // Flag to track token refresh status
//
//    @OptIn(DelicateCoroutinesApi::class)
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//        val requestBuilder = originalRequest.newBuilder()
//
//        // Add the access token if available
//        tokenManager.getAccessToken()?.let { token ->
//            requestBuilder.addHeader("Authorization", "Bearer $token")
//        }
//
//        var response = chain.proceed(requestBuilder.build())
//
//        // If the response is 401 (Unauthorized), try to refresh the token
//        if (response.code == 401 && !isRefreshing) {
//            response.close()
//            synchronized(this) {  // Synchronize the refresh logic to prevent race conditions
//                if (!isRefreshing) {
//                    isRefreshing = true  // Set flag to indicate refresh is in progress
//
//
//                    // Launch a coroutine to refresh the token asynchronously
//                    GlobalScope.launch {
//                        try {
//                            // Perform token refresh asynchronously
//                            val newAccessToken = withTimeout(5000L) {
//                                authUseCase.value.refreshTokenUseCase()
//                            }
//
//                            // If the refresh was successful, retry the request with the new token
//                            if (newAccessToken) {
//                                val newToken = tokenManager.getAccessToken() ?: return@launch
//                                val newRequest = requestBuilder
//                                    .removeHeader("Authorization")
//                                    .addHeader("Authorization", "Bearer $newToken")
//                                    .build()
//                                response = chain.proceed(newRequest)
//                            }
//                        } catch (e: Exception) {
//                            Log.e("AuthInterceptor", "Token refresh failed: ${e.message}")
//                            // Handle failure to refresh token, e.g., log out the user or show an error screen
//                        } finally {
//                            isRefreshing = false  // Reset the flag
//                        }
//                    }
//                }
//            }
//        }
//
//        return response
//    }
//}
