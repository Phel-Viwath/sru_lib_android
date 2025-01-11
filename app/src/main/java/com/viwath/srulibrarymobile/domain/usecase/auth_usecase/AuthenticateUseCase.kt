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