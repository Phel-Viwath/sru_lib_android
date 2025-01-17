/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.auth

import com.viwath.srulibrarymobile.domain.model.Email
import com.viwath.srulibrarymobile.domain.model.Password

data class ChangePasswordRequest(
    val email: Email,
    val password: Password
)
