/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state

data class ChangePasswordState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
)