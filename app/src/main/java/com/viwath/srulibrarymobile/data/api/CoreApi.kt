/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.api

import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.model.entry.Attend
import com.viwath.srulibrarymobile.domain.model.entry.Entry
import com.viwath.srulibrarymobile.domain.model.entry.EntryStatus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * CoreApi interface defines the API endpoints for the library management system.
 *
 * This interface uses Retrofit annotations to define HTTP requests and their corresponding parameters.
 * It provides methods for interacting with various parts of the system, including:
 * - Dashboard: Fetching dashboard data.
 * - Entry/Attendance: Managing student entry and attendance.
 * - Books: Managing book information, including upload, retrieval, addition, updates, and search.
 * - Trash: Managing books moved to and recovered from the trash.
 * - Language and College: Fetching available book languages and college lists.
 * - Borrowing: Managing book borrowing, including adding, fetching, extending, and searching.
 * - Donation: Managing book donations.
 */
interface CoreApi : BookApi, BorrowApi, DonationApi{
    /// core api
    @GET("/dashboard")
    suspend fun dashboard(): Response<Dashboard>

    /// Entry : http://localhost:8090/api/v1/entry
    @GET("/entry/{id}")
    suspend fun getStudentById(@Path("id") id: Long): Response<Students>

    @POST("/entry")
    suspend fun newAttend(
        @Query("entryId") studentId: String,
        @Query("purpose") purpose: String
    ): Response<Attend>

    @GET("/entry")
    suspend fun recentEntryData(): Response<Entry>

    @GET("/entry/check")
    suspend fun checkExistingStudent(@Query("entryId") entryId: String): Response<EntryStatus>

    @PUT("/entry")
    suspend fun updateExitingTime(@Query("entryId") id: Long): Response<String>

}
