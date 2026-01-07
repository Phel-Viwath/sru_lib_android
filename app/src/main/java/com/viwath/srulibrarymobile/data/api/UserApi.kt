/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("/api/v1/user/profile")
    suspend fun getProfile(
        @Query("userId") userId: String
    ): Response<Unit>
}