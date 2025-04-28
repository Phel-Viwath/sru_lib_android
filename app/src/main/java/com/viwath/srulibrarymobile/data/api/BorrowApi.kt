/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.api

import com.viwath.srulibrarymobile.data.dto.BorrowDetailDto
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.borrow.BorrowRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface BorrowApi {
    /*
   * Borrow api
   * */
    @POST("/api/v1/borrow")
    suspend fun activeBorrowDetails(@Body borrowRequest: BorrowRequest): Response<Unit>

    @GET("/api/v1/borrow/detail")
    suspend fun getAllBorrowDetails(): Response<List<BorrowDetailDto>>

    @GET("/api/v1/borrow/detail-active")
    suspend fun activeBorrowDetails(): Response<List<BorrowDetailDto>>

    @GET("/api/v1/borrow/search")
    suspend fun searchBorrow(@Query("keyword") keyword: String): Response<List<BorrowDetailDto>>

    @PUT("/api/v1/borrow/extend-borrow")
    suspend fun extendBorrow(@Query("id") id: Long): Response<Unit>

    @PUT("/api/v1/borrow")
    suspend fun returnBook(@Query("studentId") studentId: StudentId, @Query("bookId") bookId: BookId): Response<Unit>

}