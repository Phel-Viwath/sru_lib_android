package com.viwath.srulibrarymobile.data.repository

import android.util.Log
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.data.api.AuthApi
import com.viwath.srulibrarymobile.domain.model.auth.LogInRequest
import com.viwath.srulibrarymobile.domain.model.auth.RefreshToken
import com.viwath.srulibrarymobile.domain.model.auth.RegisterRequest
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
): AuthRepository {

    override suspend fun register(request: RegisterRequest): AuthResult<Unit> {
        return try {
            val response = api.register(request)
            Log.d("AuthRepositoryImp", "register: ${response.code()}")
            val loginRequest = LogInRequest(
                email = request.email,
                password = request.password
            )
            return logIn(loginRequest)
        }catch (e: Throwable){
            handleError(e)
        }
    }

    override suspend fun logIn(request: LogInRequest): AuthResult<Unit> {
        try {
            val tokenResponse = api.logIn(request)
            if (tokenResponse.isSuccessful) {
                tokenResponse.body()?.let {
                    tokenManager.saveAccessToken(it.accessToken)
                    tokenManager.saveRefreshToken(it.refreshToken)
                    tokenManager.saveUsername(it.username)
                    return AuthResult.Authorize()
                }
            }else return AuthResult.Unauthorized()
            return AuthResult.BadRequest()
        }catch (e: Throwable){
            Log.d("AuthRepository", "Error: ${e.message}")
            return handleError(e)
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return try {

            val token = tokenManager.getAccessToken()?.let {
                if (it.isBlank()) return AuthResult.BadRequest()
                else "Bearer $it"
            }
            Log.d("authenticate ", "authenticate: $token")
            val response = api.authenticate(token.toString())
            Log.d("authenticate ", "authenticate: ${response.code()}")
            Log.d("authenticate ", "authenticate error: ${response.message()}")
            if (response.isSuccessful) {
                AuthResult.Authorize()
            } else {
                AuthResult.Unauthorized()
            }
        } catch (e: Exception) {
            Log.e("AuthRepository-Authenticate", "Error:", e)
            handleError(e)
        }
    }


    override suspend fun refreshToken(): Boolean {
        val refreshToken = tokenManager.getRefreshToken() ?: return false
        return try {
            Log.d("RefreshToken", "refreshToken: Hello")
            val response = api.refreshToken(RefreshToken(refreshToken))
            Log.d("AuthRepositoryImp", "refreshToken: ${response.code()}")
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveAccessToken(it.accessToken)
                    tokenManager.saveRefreshToken(it.refreshToken)
                    true
                } ?: false
            }else{
                Log.d("AuthRepositoryImp", "refreshToken error: ${response.body()}")
            }
            false
        } catch (e: Exception) {
            Log.d("AuthRepositoryImp", "refreshToken: ${e.message}")
            false
        }
    }

    private fun <T> handleError(e: Throwable): AuthResult<T> {
        return when (e) {
            is HttpException -> when (e.code()) {
                500 -> AuthResult.InternalServerError()
                401 -> AuthResult.Unauthorized()
                400 -> AuthResult.BadRequest()
                else -> AuthResult.UnknownError()
            }
            else -> AuthResult.UnknownError()
        }
    }


}