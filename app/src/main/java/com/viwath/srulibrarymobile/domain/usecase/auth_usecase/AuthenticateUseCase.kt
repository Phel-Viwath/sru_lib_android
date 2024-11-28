package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import javax.inject.Inject

class AuthenticateUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): AuthResult<Unit>{
        try {
            val response = repository.authenticate()
            Log.d("AuthenticateUseCase", "invoke: $response")
            return if (response is AuthResult.Authorize) AuthResult.Authorize()
            else if (response is AuthResult.Unauthorized){
                val refreshToken = repository.refreshToken()
                if (refreshToken)
                    repository.authenticate()
                else AuthResult.Unauthorized()
            } else response
        } catch (e: Exception) {
            Log.e("AuthUseCase Error", "Error during authenticate: ${e.message}", e)
            throw e
        }
    }
}