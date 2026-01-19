/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.repository

import com.viwath.srulibrarymobile.data.api.CoreApi
import com.viwath.srulibrarymobile.data.safeCall
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    private val api: CoreApi
): UserRepository {
    override suspend fun getUserProfile(userId: String): Result<Unit, DataAppError.Remote> {
        return safeCall { api.getProfile(userId) }
    }
}