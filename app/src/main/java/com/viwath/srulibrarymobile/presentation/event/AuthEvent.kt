/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

sealed class AuthEvent {
    data class SignUpUsernameChanged(val value: String): AuthEvent()
    data class SignUpEmailChanged(val value: String): AuthEvent()
    data class SignUpPasswordChanged(val value: String): AuthEvent()
    data object SignUp: AuthEvent()

    data class SignInUsernameChanged(val value: String): AuthEvent()
    data class SignInPasswordChanged(val value: String): AuthEvent()
    data object SignIn: AuthEvent()
}