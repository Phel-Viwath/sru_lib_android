/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

data class AuthUseCase (
    val registerUseCase: RegisterUseCase,
    val signinUseCase: SigninUseCase,
    val authenticateUseCase: AuthenticateUseCase,
    val refreshTokenUseCase: RefreshTokenUseCase,
    val requestOtpUseCase: RequestOtpUseCase,
    val verifyOtpUseCase: VerifyOtpUseCase,
    val changePasswordUseCase: ChangePasswordUseCase
)
