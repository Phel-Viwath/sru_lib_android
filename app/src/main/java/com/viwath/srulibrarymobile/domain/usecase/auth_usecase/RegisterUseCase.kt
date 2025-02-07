/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import com.viwath.srulibrarymobile.common.exception.InvalidAuthException
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.domain.model.auth.RegisterRequest
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case responsible for handling the registration process.
 *
 * This class encapsulates the logic for registering a new user. It performs
 * validation on the provided registration data (email and password) and then
 * delegates the actual registration to the [AuthRepository].
 *
 * @property repository The [AuthRepository] instance used for communicating with
 * the data layer to perform the registration.
 */
class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    @Throws(InvalidAuthException::class)
    suspend operator fun invoke(request: RegisterRequest): AuthResult<Unit> {
        val isPasswordTooShort = request.password.length < 8
        val isUsernameFieldBlank = request.email.isBlank()
        val isPasswordFieldBlank = request.password.isBlank()
        if (isUsernameFieldBlank)
            throw InvalidAuthException("Please enter username.")
        if (isPasswordFieldBlank)
            throw InvalidAuthException("Please enter password.")
        if (isPasswordTooShort)
            throw InvalidAuthException("Password must greater than 8.")
        return repository.register(request)
    }
}