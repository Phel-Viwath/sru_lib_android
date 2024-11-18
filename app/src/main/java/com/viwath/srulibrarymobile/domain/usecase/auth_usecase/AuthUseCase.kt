package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

data class AuthUseCase (
    val registerUseCase: RegisterUseCase,
    val signinUseCase: SigninUseCase,
    val authenticateUseCase: AuthenticateUseCase
)
