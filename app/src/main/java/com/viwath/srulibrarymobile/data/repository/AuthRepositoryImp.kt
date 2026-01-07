/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.repository

import android.util.Log
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.data.api.AuthApi
import com.viwath.srulibrarymobile.domain.model.auth.ChangePasswordRequest
import com.viwath.srulibrarymobile.domain.model.auth.LogInRequest
import com.viwath.srulibrarymobile.domain.model.auth.RefreshToken
import com.viwath.srulibrarymobile.domain.model.auth.RegisterRequest
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import com.viwath.srulibrarymobile.utils.datastore.UserPreferences
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Implementation of the [AuthRepository] interface.
 *
 * This class handles all authentication-related operations, including:
 * - User registration
 * - User login
 * - User authentication (checking if the user is logged in)
 * - Refreshing the user's access token
 * - Requesting OTP
 * - Verifying OTP
 * - Changing Password
 *
 * It utilizes the [AuthApi] for network requests and the [UserPreferences] for managing
 * authentication tokens. It also includes error handling and logging.
 *
 * @property api The [AuthApi] instance used to make network requests to the authentication server.
 * @property userPreferences The [UserPreferences] instance used to manage authentication tokens.
 */

class AuthRepositoryImp @Inject constructor(
    private val api: AuthApi,
    private val userPreferences: UserPreferences
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
                    userPreferences.saveAccessToken(it.accessToken)
                    userPreferences.saveRefreshToken(it.refreshToken)
                    userPreferences.saveUserId(it.userId)
                    userPreferences.saveRole(it.role)
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
            val tokens = userPreferences.getAccessToken()
            val accessToken: String
            if (tokens == null){
                return AuthResult.BadRequest()
            }else{
                accessToken = "Bearer $tokens"
            }
            val response = api.authenticate(accessToken)
            Log.d("Authenticate", "authenticate response: ${response.code()}")
            if (response.isSuccessful) AuthResult.Authorize()
            else AuthResult.Unauthorized()
        } catch (e: Exception) {
            Log.e("AuthRepository-Authenticate", "Error: ${e.message}", e.cause)
            handleError(e)
        }
    }


    override suspend fun refreshToken(): Boolean {
        val refreshToken = userPreferences.getRefreshToken()
        return try {
            if (refreshToken == null) return false
            val response = api.refreshToken(RefreshToken(refreshToken))
            if (response.isSuccessful) {
                response.body()?.let {
                    userPreferences.saveUserId(it.userId)
                    userPreferences.saveAccessToken(it.accessToken)
                    userPreferences.saveRefreshToken(it.refreshToken)
                    userPreferences.saveRole(it.role)
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            Log.d("AuthRepositoryImp", "refreshToken: ${e.message}")
            false
        }
    }

    override suspend fun requestOtp(email: String): Boolean {
        val response = api.requestOtp(email)
        return response.isSuccessful
    }

    override suspend fun verifyOtp(otp: String): Boolean {
        val response = api.verifyOtp(otp)
        return response.isSuccessful
    }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Boolean {
        val response = api.changePassword(changePasswordRequest)
        return response.isSuccessful
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