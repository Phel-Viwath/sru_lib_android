package com.viwath.srulibrarymobile.presentation.event

data class AuthState(
    val signInEmail: String = "",
    val signInPassword: String = "",
    val signUpEmail: String = "",
    val signUpPassword: String = "",
    val signUpUsername: String = ""
)