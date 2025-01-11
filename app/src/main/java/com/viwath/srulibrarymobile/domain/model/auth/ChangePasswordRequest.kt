/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.auth

data class ChangePasswordRequest(
    val email: String,
    val password: String
)
