package com.viwath.srulibrarymobile.data.repository

import android.util.Log
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.data.api.RemoteApi
import com.viwath.srulibrarymobile.domain.model.auth.LogInRequest
import com.viwath.srulibrarymobile.domain.model.auth.RefreshToken
import com.viwath.srulibrarymobile.domain.model.auth.RegisterRequest
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val remoteApi: RemoteApi,
    private val tokenManager: TokenManager
): AuthRepository {

    override suspend fun register(request: RegisterRequest): AuthResult<Unit> {
        return try {
            val response = remoteApi.register(request)
            Log.d("VIWATH", "register: $response")
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
            val tokenResponse = remoteApi.logIn(request)
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
            Log.e("AuthRepository", "Error:", e)
            return handleError(e)
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        val response = remoteApi.authenticate()
        Log.d("authenticate ", "authenticate: ${response.code()}")
        return try {

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
            val response = remoteApi.refreshToken(RefreshToken(refreshToken))
            Log.d("AuthRepositoryImp", "refreshToken: ${response.code()}")
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveAccessToken(it.accessToken)
                    tokenManager.saveRefreshToken(it.refreshToken)
                    true
                } ?: false
            }
            false
        } catch (e: Exception) {
            Log.e("AuthRepositoryImp", "refreshToken: ${e.message}")
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