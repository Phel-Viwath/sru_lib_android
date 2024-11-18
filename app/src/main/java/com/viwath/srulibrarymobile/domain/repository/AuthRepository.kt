package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.domain.model.auth.LogInRequest
import com.viwath.srulibrarymobile.domain.model.auth.RegisterRequest
import com.viwath.srulibrarymobile.common.result.AuthResult

interface AuthRepository {
    suspend fun register(request: RegisterRequest): AuthResult<Unit>
    suspend fun logIn(request: LogInRequest): AuthResult<Unit>
    suspend fun authenticate(): AuthResult<Unit>
    suspend fun refreshToken(): Boolean
}