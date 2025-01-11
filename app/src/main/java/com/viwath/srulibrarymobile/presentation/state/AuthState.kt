/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state

data class AuthState(
    val signInEmail: String = "",
    val signInPassword: String = "",
    val signUpEmail: String = "",
    val signUpPassword: String = "",
    val signUpUsername: String = "",
)