/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

sealed class ChangePasswordEvent {
    data object OnChangePassword: ChangePasswordEvent()
    data class NewPasswordChange(val newPassword: String): ChangePasswordEvent()
    data class ConfirmPasswordChange(val confirmPassword: String): ChangePasswordEvent()
    data class EmailChange(val email: String): ChangePasswordEvent()
}