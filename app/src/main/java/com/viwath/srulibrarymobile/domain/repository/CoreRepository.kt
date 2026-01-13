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
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BorrowId
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
import okhttp3.MultipartBody

/**
 * CoreRepository interface provides access to the core data operations.
 * This interface defines methods for interacting with various data sources,
 * including dashboards, students, attendance, books, borrows, and donations.
 */
interface CoreRepository {
    suspend fun getDashboard(): Dashboard
    suspend fun getStudentById(id: Long): Result<Students, DataAppError.Remote>
    suspend fun newAttend(studentId: String, purpose: String): Attend
    suspend fun getRecentEntryData(): Entry
    suspend fun updateExitingTime(studentId: Long): Boolean
    suspend fun checkExitingAttend(id: String): String

    // book
    suspend fun addBooks(books: List<Book>): Result<Unit, DataAppError.Remote>
    suspend fun uploadBook(file: MultipartBody.Part): Result<Unit, DataAppError.Remote>
    suspend fun updateBook(book: Book): Result<Unit, DataAppError.Remote>
    suspend fun getBooks(): Result<List<BookDto>, DataAppError.Remote>
    suspend fun getBooksInTrash(): Result<List<BookDto>, DataAppError.Remote>
    suspend fun getSummaryBook(): Result<BookSummary, DataAppError.Remote>
    suspend fun moveToTrash(bookId: BookId): Result<Unit, DataAppError.Remote>
    suspend fun recoverBook(bookId: BookId): Result<Unit, DataAppError.Remote>
    suspend fun bookLanguages(): Result<List<Language>, DataAppError.Remote>
    suspend fun college(): Result<List<College>, DataAppError.Remote>
    suspend fun searchBook(keyword: String): Result<List<BookDto>, DataAppError.Remote>
    suspend fun deleteBook(bookId: BookId): Result<Unit, DataAppError.Remote>

    // borrow
    suspend fun borrowBook(borrow: BorrowRequest): Result<Unit, DataAppError.Remote>
    suspend fun getAllBorrowsDetail(): Result<List<BorrowDetailDto>, DataAppError.Remote>
    suspend fun getActiveBorrowsDetail(): Result<List<BorrowDetailDto>, DataAppError.Remote>
    suspend fun searchBorrow(keyword: String): Result<List<BorrowDetailDto>, DataAppError.Remote>
    suspend fun extendBorrow(id: BorrowId): Result<Unit, DataAppError.Remote>
    suspend fun returnBook(studentId: StudentId, bookId: BookId): Result<Unit, DataAppError.Remote>

    // donation
    suspend fun addDonation(donationIO: DonationIO): Result<Unit, DataAppError.Remote>
    suspend fun getAllDonation(): Result<List<DonationDto>, DataAppError.Remote>
    suspend fun updateDonation(donationIO: DonationIO): Result<Unit, DataAppError.Remote>

    // user
    suspend fun getUser(userId: String): Result<Unit, DataAppError.Remote>
}