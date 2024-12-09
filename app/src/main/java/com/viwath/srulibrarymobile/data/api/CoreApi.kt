package com.viwath.srulibrarymobile.data.api

import com.viwath.srulibrarymobile.domain.model.Attend
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.model.entry.Entry
import com.viwath.srulibrarymobile.domain.model.entry.EntryStatus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CoreApi {

    /// core api
    @GET("/api/v1/dashboard")
    suspend fun dashboard(): Response<Dashboard>

    /// Entry : http://localhost:8090/api/v1/entry
    @GET("/api/v1/entry/{id}")
    suspend fun getStudentById(@Path("id") id: Long): Response<Students>

    @POST("/api/v1/entry")
    suspend fun newAttend(
        @Query("entryId") studentId: String,
        @Query("purpose") purpose: String
    ): Response<Attend>

    @GET("/api/v1/entry")
    suspend fun recentEntryData(): Response<Entry>

    @GET("/api/v1/entry/check")
    suspend fun checkExistingStudent(@Query("entryId") entryId: String): Response<EntryStatus>

    @PUT("/api/v1/entry")
    suspend fun updateExitingTime(@Query("entryId") id: Long): Response<String>


}