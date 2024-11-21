package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean{
        return repository.refreshToken()
    }
}