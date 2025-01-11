/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.domain.model.Attend
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.model.entry.Entry
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.File

interface CoreRepository {
    suspend fun getDashboard(): Dashboard
    suspend fun getStudentById(id: Long): Students
    suspend fun newAttend(studentId: String, purpose: String): Attend
    suspend fun getRecentEntryData(): Entry
    suspend fun updateExitingTime(studentId: Long): Boolean
    suspend fun checkExitingAttend(id: String): String

    suspend fun addBooks(books: List<BookDto>): Boolean
    suspend fun uploadBook(file: MultipartBody.Part): Response<Unit>
    suspend fun updateBook(book: BookDto): Boolean
    suspend fun getBooks(): List<BookDto>
    suspend fun getBooksInTrash(): List<BookDto>
    suspend fun getSummaryBook(): BookSummary
    suspend fun moveToTrash(bookId: String): Boolean
    suspend fun recoverBook(bookId: String): Boolean
    suspend fun bookLanguages(): List<Language>
    suspend fun college(): List<College>
}