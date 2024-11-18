package com.viwath.srulibrarymobile.domain.model.auth

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
