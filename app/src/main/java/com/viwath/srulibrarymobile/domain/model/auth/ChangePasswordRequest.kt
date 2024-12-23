package com.viwath.srulibrarymobile.domain.model.auth

data class ChangePasswordRequest(
    val email: String,
    val password: String
)
