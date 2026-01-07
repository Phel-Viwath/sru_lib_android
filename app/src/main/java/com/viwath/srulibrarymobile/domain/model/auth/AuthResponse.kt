/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.auth

import com.viwath.srulibrarymobile.domain.model.AccessToken
import com.viwath.srulibrarymobile.domain.model.Message
import com.viwath.srulibrarymobile.domain.model.RefreshToken
import com.viwath.srulibrarymobile.domain.model.Role
import com.viwath.srulibrarymobile.domain.model.UserId
import com.viwath.srulibrarymobile.domain.model.Username

data class AuthResponse (
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val userId: UserId,
    val role: Role,
    val message: Message?
)