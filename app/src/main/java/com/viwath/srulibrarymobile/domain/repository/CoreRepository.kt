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
import com.viwath.srulibrarymobile.domain.model.entry.Entry
import okhttp3.MultipartBody

/**
 * CoreRepository interface provides access to the core data operations.
 * This interface defines methods for interacting with various data sources,
 * including dashboards, students, attendance, books, borrows, and donations.
 */
interface CoreRepository : AttendRepository, BookRepository, BorrowRepository, DonationRepository {
    suspend fun getDashboard(): Dashboard

    // user
    suspend fun getUser(userId: String): Result<Unit, DataAppError.Remote>
}