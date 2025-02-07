/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

/**
 * `AuthUseCase` is a data class that encapsulates all authentication-related use cases.
 * It provides a single entry point for interacting with various authentication flows,
 * such as registration, sign-in, authentication, token refresh, OTP management, and password changes.
 *
 * This class acts as an aggregate or facade, centralizing the different authentication functionalities.
 * By grouping these use cases together, it simplifies the interaction with the authentication
 * system from other parts of the application.
 *
 * @property registerUseCase Use case for user registration. Handles the logic for creating a new user account.
 * @property signinUseCase Use case for user sign-in. Handles the logic for user authentication with existing credentials.
 * @property authenticateUseCase Use case for authenticating a user. This might involve checking for valid tokens or sessions.
 * @property refreshTokenUseCase Use case for refreshing authentication tokens. Handles the logic for obtaining new tokens when existing ones expire.
 * @property requestOtpUseCase Use case for requesting a One-Time Password (OTP). Handles the logic for generating and sending OTPs.
 * @property verifyOtpUseCase Use case for verifying an OTP. Handles the logic for validating the OTP provided by the user.
 * @property changePasswordUseCase Use case for changing a user's password. Handles the logic for updating the user's password in the system.
 */
data class AuthUseCase (
    val registerUseCase: RegisterUseCase,
    val signinUseCase: SigninUseCase,
    val authenticateUseCase: AuthenticateUseCase,
    val refreshTokenUseCase: RefreshTokenUseCase,
    val requestOtpUseCase: RequestOtpUseCase,
    val verifyOtpUseCase: VerifyOtpUseCase,
    val changePasswordUseCase: ChangePasswordUseCase
)
