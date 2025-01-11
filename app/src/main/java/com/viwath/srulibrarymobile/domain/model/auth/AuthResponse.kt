/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.auth

data class AuthResponse (
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val role: String,
    val message: String?
)