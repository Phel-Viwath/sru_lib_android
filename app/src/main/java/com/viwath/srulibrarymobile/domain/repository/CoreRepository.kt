/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.data.dto.BorrowDetailDto
import com.viwath.srulibrarymobile.data.dto.DonationDto
import com.viwath.srulibrarymobile.domain.DataError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.domain.model.book.Book
import com.viwath.srulibrarymobile.domain.model.borrow.BorrowRequest
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.model.entry.Attend
import com.viwath.srulibrarymobile.domain.model.entry.Entry
import okhttp3.MultipartBody
import retrofit2.Response

/**
 * CoreRepository interface provides access to the core data operations.
 * This interface defines methods for interacting with various data sources,
 * including dashboards, students, attendance, books, borrows, and donations.
 */
interface CoreRepository {
    suspend fun getDashboard(): Dashboard
    suspend fun getStudentById(id: Long): Students
    suspend fun newAttend(studentId: String, purpose: String): Attend
    suspend fun getRecentEntryData(): Entry
    suspend fun updateExitingTime(studentId: Long): Boolean
    suspend fun checkExitingAttend(id: String): String

    // book
    suspend fun addBooks(books: List<Book>): Boolean
    suspend fun uploadBook(file: MultipartBody.Part): Response<Unit>
    suspend fun updateBook(book: Book): Boolean
    suspend fun getBooks(): List<BookDto>
    suspend fun getBooksInTrash(): List<BookDto>
    suspend fun getSummaryBook(): BookSummary
    suspend fun moveToTrash(bookId: String): Boolean
    suspend fun recoverBook(bookId: String): Boolean
    suspend fun bookLanguages(): List<Language>
    suspend fun college(): List<College>
    suspend fun searchBook(keyword: String): List<BookDto>

    // borrow
    suspend fun borrowBook(borrow: BorrowRequest): Response<Unit>
    suspend fun getAllBorrowsDetail(): List<BorrowDetailDto>
    suspend fun getActiveBorrowsDetail(): List<BorrowDetailDto>
    suspend fun searchBorrow(keyword: String): List<BorrowDetailDto>
    suspend fun extendBorrow(id: BorrowId): Response<Unit>
    suspend fun returnBook(studentId: StudentId, bookId: BookId): Response<Unit>

    // donation
    suspend fun addDonation(donationDto: DonationDto): Result<Unit, DataError.Remote>
    suspend fun getAllDonation(): Result<List<DonationDto>, DataError.Remote>
    suspend fun updateDonation(donationDto: DonationDto): Result<Unit, DataError.Remote>
}