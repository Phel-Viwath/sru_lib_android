/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import com.viwath.srulibrarymobile.common.exception.InvalidAuthException
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.domain.model.auth.LogInRequest
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * `SigninUseCase` is a use case class responsible for handling the sign-in logic.
 *
 * It takes an [AuthRepository] instance in its constructor and exposes an `invoke`
 * operator function that allows initiating the sign-in process.
 *
 * The sign-in process involves:
 * 1. Validating the provided email and password.
 * 2. Invoking the [AuthRepository.logIn] method to perform the actual sign-in operation.
 *
 * @property repository The [AuthRepository] instance used to interact with the data layer for authentication.
 */
class SigninUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    @Throws(InvalidAuthException::class)
    suspend operator fun invoke(request: LogInRequest): AuthResult<Unit> {
        if (request.email.isBlank())
            throw InvalidAuthException("Please Enter Username.")
        if (request.password.isBlank())
            throw InvalidAuthException("Please Enter Password.")
        return repository.logIn(request)
    }
}