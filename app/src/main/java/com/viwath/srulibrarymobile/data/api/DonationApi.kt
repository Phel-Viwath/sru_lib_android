/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.api

import com.viwath.srulibrarymobile.data.dto.DonationDto
import com.viwath.srulibrarymobile.domain.model.DonationIO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface DonationApi {

    @GET("/api/v1/donation")
    suspend fun getAllDonation(): Response<List<DonationDto>>

    @POST("/api/v1/donation")
    suspend fun addDonation(@Body donationIO: DonationIO): Response<Unit>

    @PUT("/api/v1/donation")
    suspend fun updateDonation(@Body donationDto: DonationIO): Response<Unit>

}