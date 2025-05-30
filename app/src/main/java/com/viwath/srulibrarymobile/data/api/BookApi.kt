/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.api

import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.model.book.Book
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApi {
    @Multipart @POST("api/v1/upload/book")
    suspend fun uploadBook(@Part file: MultipartBody.Part): Response<Unit>

    @GET("api/v1/book/about-book-data")
    suspend fun getSummaryBook(): Response<BookSummary>

    @POST("api/v1/book")
    suspend fun addBooks(@Body books: List<Book>): Response<Unit>

    @GET("api/v1/book/current-book")
    suspend fun getBooks(): Response<List<BookDto>>

    @PUT("api/v1/book")
    suspend fun updateBook(@Body book: Book): Response<Unit>

    @GET("api/v1/book/search")
    suspend fun searchBook(@Query("keyword") keyword: String): Response<List<BookDto>>

    @PUT("api/v1/book/trash")
    suspend fun movToTrash(@Query("bookId") bookId: BookId): Response<Unit>

    @PUT("api/v1/book/recover")
    suspend fun recoverBook(@Query("bookId") bookId: BookId): Response<Unit>

    @DELETE("api/v1/book/{bookId}")
    suspend fun deleteBook(@Path("bookId") bookId: BookId): Response<Unit>

    @GET("api/v1/book/in-trash")
    suspend fun bookInTrash(): Response<List<BookDto>>

    @GET("api/v1/language")
    suspend fun bookLanguage(): Response<List<Language>>

    @GET("api/v1/college")
    suspend fun college(): Response<List<College>>
}