package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.Attend
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.model.entry.Entry
import kotlinx.coroutines.flow.Flow

interface CoreRepository {
    suspend fun getDashboard(): Dashboard
    suspend fun getStudentById(id: Long): Students
    suspend fun newAttend(studentId: String, purpose: String): Attend
    suspend fun getRecentEntryData(): Entry
    suspend fun updateExitingTime(studentId: Long): Boolean
    suspend fun checkExitingAttend(id: String): String

    suspend fun addBooks(books: List<BookDto>): BookDto
    suspend fun updateBook(book: BookDto): BookDto
    suspend fun getBooks(): List<BookDto>
    suspend fun getBooksInTrash(): List<BookDto>
    suspend fun getSummaryBook(): BookSummary
    suspend fun moveToTrash(bookId: String): Boolean
    suspend fun recoverBook(bookId: String): Boolean
}