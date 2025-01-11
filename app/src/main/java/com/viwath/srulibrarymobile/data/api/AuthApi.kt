/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.api

import com.viwath.srulibrarymobile.domain.model.auth.AuthResponse
import com.viwath.srulibrarymobile.domain.model.auth.ChangePasswordRequest
import com.viwath.srulibrarymobile.domain.model.auth.LogInRequest
import com.viwath.srulibrarymobile.domain.model.auth.RefreshToken
import com.viwath.srulibrarymobile.domain.model.auth.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthApi {
    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/api/v1/auth/login")
    suspend fun logIn(@Body request: LogInRequest): Response<AuthResponse>

    @POST("/api/v1/auth/refresh-token")
    suspend fun refreshToken(@Body refreshToken: RefreshToken): Response<AuthResponse>

    @GET("/api/v1/welcome")
    suspend fun authenticate(@Header("Authorization") token: String): Response<Unit>

    @POST("/api/v1/auth/otp")
    suspend fun requestOtp(@Query("email") email: String): Response<Unit>

    @POST("/api/v1/auth/verify")
    suspend fun verifyOtp(@Query("otp") otp: String): Response<Unit>

    @PUT("/api/v1/auth/change-password")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Response<Unit>
}