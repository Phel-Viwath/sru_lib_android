/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.auth

import com.viwath.srulibrarymobile.domain.model.Email
import com.viwath.srulibrarymobile.domain.model.Password
import com.viwath.srulibrarymobile.domain.model.Username

data class RegisterRequest(
    val email: Email,
    val username: Username,
    val password: Password
)
