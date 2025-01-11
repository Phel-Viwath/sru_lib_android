/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.auth

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)
