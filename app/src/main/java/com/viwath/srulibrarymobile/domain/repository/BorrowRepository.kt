/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.data.dto.BorrowDetailDto
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.borrow.BorrowRequest

interface BorrowRepository {
    // borrow
    suspend fun borrowBook(borrow: BorrowRequest): Result<Unit, DataAppError.Remote>
    suspend fun getAllBorrowsDetail(): Result<List<BorrowDetailDto>, DataAppError.Remote>
    suspend fun getActiveBorrowsDetail(): Result<List<BorrowDetailDto>, DataAppError.Remote>
    suspend fun searchBorrow(keyword: String): Result<List<BorrowDetailDto>, DataAppError.Remote>
    suspend fun extendBorrow(id: BorrowId): Result<Unit, DataAppError.Remote>
    suspend fun returnBook(studentId: StudentId, bookId: BookId): Result<Unit, DataAppError.Remote>
}