package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import com.viwath.srulibrarymobile.common.exception.InvalidAuthException
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.domain.model.auth.RegisterRequest
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    @Throws(InvalidAuthException::class)
    suspend operator fun invoke(request: RegisterRequest): AuthResult<Unit> {
        val isPasswordTooShort = request.password.length < 8
        val isUsernameFieldBlank = request.username.isBlank() && request.password.isNotBlank()
        val isPasswordFieldBlank = request.username.isNotBlank() && request.password.isBlank()
        val areFieldBlank = request.username.isBlank() || request.password.isBlank()
        if (isUsernameFieldBlank)
            throw InvalidAuthException("Please enter username.")
        if (isPasswordFieldBlank)
            throw InvalidAuthException("Please enter password.")
        if (areFieldBlank)
            throw InvalidAuthException("Field can not be blank.")
        if (isPasswordTooShort)
            throw InvalidAuthException("Password must greater than 8.")
        return repository.register(request)
    }
}