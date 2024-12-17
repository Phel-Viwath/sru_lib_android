package com.viwath.srulibrarymobile.data.api

import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.domain.model.Attend
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.model.entry.Entry
import com.viwath.srulibrarymobile.domain.model.entry.EntryStatus
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
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


    //http://localhost:8090/api/v1/book update get save
    // about_book /about-book-data
    //PUT("/trash", bookHandler::moveToTrash)
    // PUT("/recover", bookHandler::recoverBook)
    // GET("/in-trash", bookHandler::getBooksInTrash)

    @GET("/api/v1/book/about-book-data")
    suspend fun getSummaryBook(): Response<BookSummary>
    @POST("/api/v1/book")
    suspend fun addBooks(@Body books: List<BookDto>): Response<BookDto>
    @GET("/api/v1/book")
    fun getBooks(): Response<Flow<BookDto>>
    @PUT("/api/v1/book")
    suspend fun updateBook(@Body book: BookDto): Response<BookDto>
    @PUT("/api/v1/trash")
    suspend fun movToTrash(@Query("bookId") bookId: String): Response<String>
    @PUT("/api/v1/recover")
    suspend fun recoverBook(@Query("bookId") bookId: String): Response<String>
    @GET("api/v1/in-trash")
    fun bookInTrash(): Response<Flow<BookDto>>
}