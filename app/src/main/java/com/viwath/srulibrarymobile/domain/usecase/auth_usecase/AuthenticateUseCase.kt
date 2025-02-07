/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * `AuthenticateUseCase` is a use case responsible for handling the authentication logic.
 * It interacts with the `AuthRepository` to perform authentication and refresh token operations.
 *
 * This use case abstracts the complexities of authentication and token refreshing
 * from the higher-level components (e.g., ViewModels).
 *
 * @property repository An instance of `AuthRepository` for interacting with authentication-related data.
 */
class AuthenticateUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): AuthResult<Unit> {
        return try {
            when (val response = repository.authenticate()) {
                is AuthResult.Authorize -> AuthResult.Authorize()
                is AuthResult.Unauthorized -> {
                    val refreshTokenSuccess = repository.refreshToken()
                    if (refreshTokenSuccess) repository.authenticate()
                    else AuthResult.Unauthorized()
                }
                else -> response
            }
        } catch (e: Exception) {
            Log.e("AuthUseCase Error", "Error during authenticate: ${e.message}", e)
            throw e
        }
    }

}