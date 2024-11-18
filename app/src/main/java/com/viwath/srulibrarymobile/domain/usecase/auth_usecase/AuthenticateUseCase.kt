package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.exception.CoreException
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import javax.inject.Inject

class AuthenticateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult<Unit>{
        Log.d("AuthUseCase", "authenticateUseCase() called")
        try {
            val response = repository.authenticate() // Replace with your API call
            Log.d("AuthUseCase Response", "authenticate response: $response")
            return response
        } catch (e: Exception) {
            Log.e("AuthUseCase Error", "Error during authenticate: ${e.message}", e)
            throw e
        }
    }
}