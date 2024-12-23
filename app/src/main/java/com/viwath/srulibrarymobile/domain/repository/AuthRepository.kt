package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.domain.model.auth.LogInRequest
import com.viwath.srulibrarymobile.domain.model.auth.RegisterRequest
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.domain.model.auth.ChangePasswordRequest

interface AuthRepository {
    suspend fun register(request: RegisterRequest): AuthResult<Unit>
    suspend fun logIn(request: LogInRequest): AuthResult<Unit>
    suspend fun authenticate(): AuthResult<Unit>
    suspend fun refreshToken(): Boolean
    suspend fun requestOtp(email: String): Boolean
    suspend fun verifyOtp(otp: String): Boolean
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Boolean
}