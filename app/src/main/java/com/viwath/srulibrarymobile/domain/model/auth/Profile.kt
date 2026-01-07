/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.auth

import com.viwath.srulibrarymobile.domain.model.Role

data class Profile(
    val userId: String? = null,
    val username: String,
    val email: String,
    val role: Role
)
