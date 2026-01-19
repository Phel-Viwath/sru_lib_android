/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result

interface UserRepository {
    // user
    suspend fun getUserProfile(userId: String): Result<Unit, DataAppError.Remote>
}