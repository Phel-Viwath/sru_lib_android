/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.api

import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.data.dto.BorrowDetailDto
import com.viwath.srulibrarymobile.data.dto.DonationDto
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.DonationIO
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.domain.model.book.Book
import com.viwath.srulibrarymobile.domain.model.borrow.BorrowRequest
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.model.entry.Attend
import com.viwath.srulibrarymobile.domain.model.entry.Entry
import com.viwath.srulibrarymobile.domain.model.entry.EntryStatus
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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


    //http://localhost:8090/api/v1/book update get save
    // about_book /about-book-data
    //PUT("/trash", bookHandler::moveToTrash)
    // PUT("/recover", bookHandler::recoverBook)
    // GET("/in-trash", bookHandler::getBooksInTrash)

    @Multipart @POST("/api/v1/upload/book")
    suspend fun uploadBook(@Part file: MultipartBody.Part): Response<Unit>
    
    @GET("/api/v1/book/about-book-data")
    suspend fun getSummaryBook(): Response<BookSummary>

    @POST("/api/v1/book")
    suspend fun addBooks(@Body books: List<Book>): Response<Unit>

    @GET("/api/v1/book/current-book")
    suspend fun getBooks(): Response<List<BookDto>>

    @PUT("/api/v1/book")
    suspend fun updateBook(@Body book: Book): Response<Unit>

    @GET("/api/v1/book/search")
    suspend fun searchBook(@Query("keyword") keyword: String): Response<List<BookDto>>

    @PUT("/api/v1/book/trash")
    suspend fun movToTrash(@Query("bookId") bookId: String): Response<Unit>

    @PUT("/api/v1/recover")
    suspend fun recoverBook(@Query("bookId") bookId: String): Response<Unit>

    @GET("/api/v1/in-trash")
    suspend fun bookInTrash(): Response<List<BookDto>>

    @GET("/api/v1/language")
    suspend fun bookLanguage(): Response<List<Language>>

    @GET("/api/v1/college")
    suspend fun college(): Response<List<College>>

    /*
    * Borrow api
    * */
    @POST("/api/v1/borrow")
    suspend fun activeBorrowDetails(@Body borrowRequest: BorrowRequest): Response<Unit>

    @GET("api/v1/borrow/detail")
    suspend fun getAllBorrowDetails(): Response<List<BorrowDetailDto>>

    @GET("api/v1/borrow/detail-active")
    suspend fun activeBorrowDetails(): Response<List<BorrowDetailDto>>

    @GET("api/v1/borrow/search")
    suspend fun searchBorrow(@Query("keyword") keyword: String): Response<List<BorrowDetailDto>>

    @PUT("api/v1/borrow/extend-borrow")
    suspend fun extendBorrow(@Query("id") id: Long): Response<Unit>

    @PUT("/api/v1/borrow")
    suspend fun returnBook(@Query("studentId") studentId: StudentId, @Query("bookId") bookId: BookId): Response<Unit>

    @GET("/api/v1/donation")
    suspend fun getAllDonation(): Response<List<DonationDto>>

    @POST("/api/v1/donation")
    suspend fun addDonation(@Body donationIO: DonationIO): Response<Unit>

    @PUT("api/v1/donation")
    suspend fun updateDonation(@Body donationDto: DonationIO): Response<Unit>

}