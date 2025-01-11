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