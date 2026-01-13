/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.repository

import android.util.Log
import com.viwath.srulibrarymobile.common.exception.CoreException
import com.viwath.srulibrarymobile.data.api.CoreApi
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.data.dto.BorrowDetailDto
import com.viwath.srulibrarymobile.data.dto.DonationDto
import com.viwath.srulibrarymobile.data.safeCall
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
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * Implementation of the [CoreRepository] interface.
 * This class handles data operations related to core functionalities,
 * such as fetching dashboard information, managing student data,
 * handling attendances, book management, and donation features.
 *
 * @property api The [CoreApi] instance used for making network requests.
 */
class CoreRepositoryImp @Inject constructor(
    private val api: CoreApi
) : CoreRepository {
    override suspend fun getDashboard(): Dashboard {
        val response = api.dashboard()
        Log.d("Dashboard", "Response body: $response")
        return if (response.isSuccessful){
            response.body()?.let {
                Dashboard(
                    it.cardData,
                    it.totalMajorVisitor,
                    it.weeklyVisitor,
                    it.bookAvailable,
                    it.customEntry
                )
            } ?: throw CoreException("Response body is null")
        }
        else throw CoreException("Error: Dashboard")
    }

    override suspend fun getStudentById(id: Long): Result<Students, DataAppError.Remote> {
        return safeCall { api.getStudentById(id) }
    }

    override suspend fun newAttend(studentId: String, purpose: String): Attend {
        val response = api.newAttend(studentId, purpose)
        if (!response.isSuccessful)
            throw CoreException("Error: new attend ${response.code()} + ${response.body()}")
        return response.body()?.let {
            Attend(
                attendId = it.attendId,
                studentId = it.studentId,
                entryTimes = it.entryTimes,
                exitingTimes = it.exitingTimes,
                date = it.date,
                purpose = it.purpose
            )
        } ?: throw CoreException("new attend null")
    }
    override suspend fun getRecentEntryData(): Entry {
        val response = api.recentEntryData()
        return if (response.isSuccessful){
            response.body()?.let {
                Entry(
                    cardEntry = it.cardEntry,
                    attendDetail = it.attendDetail
                )
            } ?: throw CoreException("Body is null")
        } else throw CoreException("")
    }

    override suspend fun updateExitingTime(studentId: Long): Boolean {
        val response = api.updateExitingTime(studentId)
        if (response.isSuccessful) return true
        else throw CoreException("${response.body()} ${response.code()}")
    }

    override suspend fun checkExitingAttend(id: String): String {
        val response = api.checkExistingStudent(id)
        Log.e("CoreRepositoryImp", "checkExitingAttend: $response")
        if (!response.isSuccessful)
            throw CoreException("Network Error!, ${response.code()} + $id + ${response.message()}")
        val body = response.body()
        Log.d("CheckExitingAttend", "Response body: $body")
        return body?.status ?: throw CoreException("Response body is null")
    }

    override suspend fun addBooks(books: List<Book>): Result<Unit, DataAppError.Remote> {
        return safeCall { api.addBooks(books) }
    }

    override suspend fun uploadBook(file: MultipartBody.Part): Result<Unit, DataAppError.Remote> {
        return safeCall { api.uploadBook(file) }
    }

    override suspend fun updateBook(book: Book): Result<Unit, DataAppError.Remote> {
        return safeCall { api.updateBook(book) }
    }

    override suspend fun getBooks(): Result<List<BookDto>, DataAppError.Remote> {
        return safeCall{ api.getBooks() }
    }

    override suspend fun getBooksInTrash(): Result<List<BookDto>, DataAppError.Remote> {
        val data =  safeCall { api.bookInTrash() }
        Log.d("CoreRepositoryImp", "getBooksInTrash: $data")
        return data
    }

    override suspend fun getSummaryBook(): Result<BookSummary, DataAppError.Remote>{
        return safeCall { api.getSummaryBook() }
    }

    override suspend fun moveToTrash(bookId: String): Result<Unit, DataAppError.Remote> {
        return safeCall { api.movToTrash(bookId) }
    }

    override suspend fun recoverBook(bookId: String): Result<Unit, DataAppError.Remote> {
        return safeCall { api.recoverBook(bookId) }
    }

    override suspend fun deleteBook(bookId: BookId): Result<Unit, DataAppError.Remote> {
        return safeCall { api.deleteBook(bookId) }
    }

    override suspend fun bookLanguages(): Result<List<Language>, DataAppError.Remote> {
        return safeCall { api.bookLanguage() }
    }

    override suspend fun college(): Result<List<College>, DataAppError.Remote> {
        return safeCall { api.college() }
    }

    override suspend fun searchBook(
        keyword: String
    ): Result<List<BookDto>, DataAppError.Remote> {
        return safeCall { api.searchBook(keyword) }
    }

    override suspend fun borrowBook(borrow: BorrowRequest): Result<Unit, DataAppError.Remote> {
        return safeCall { api.activeBorrowDetails(borrow) }
    }

    override suspend fun getAllBorrowsDetail(): Result<List<BorrowDetailDto>, DataAppError.Remote> {
        return safeCall { api.getAllBorrowDetails() }
    }

    override suspend fun getActiveBorrowsDetail(): Result<List<BorrowDetailDto>, DataAppError.Remote> {
        return safeCall { api.activeBorrowDetails() }
    }

    override suspend fun searchBorrow(keyword: String): Result<List<BorrowDetailDto>, DataAppError.Remote> {
        return safeCall { api.searchBorrow(keyword) }
    }

    override suspend fun extendBorrow(id: BorrowId): Result<Unit, DataAppError.Remote> {
        return safeCall { api.extendBorrow(id) }
    }

    override suspend fun returnBook(
        studentId: StudentId,
        bookId: BookId
    ): Result<Unit, DataAppError.Remote> {
        return safeCall { api.returnBook(studentId, bookId) }
    }

    // donation

    override suspend fun addDonation(donationIO: DonationIO): Result<Unit, DataAppError.Remote> {
        return safeCall { api.addDonation(donationIO) }
    }

    override suspend fun getAllDonation(): Result<List<DonationDto>, DataAppError.Remote> {
        return safeCall { api.getAllDonation() }
    }

    override suspend fun updateDonation(donationIO: DonationIO): Result<Unit, DataAppError.Remote> {
        return safeCall { api.updateDonation(donationIO) }
    }

    override suspend fun getUser(userId: String): Result<Unit, DataAppError.Remote> {
        return safeCall { api.getProfile(userId) }
    }

}