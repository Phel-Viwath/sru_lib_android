/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.repository

import com.viwath.srulibrarymobile.data.api.CoreApi
import com.viwath.srulibrarymobile.data.dto.BorrowDetailDto
import com.viwath.srulibrarymobile.data.safeCall
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.borrow.BorrowRequest
import com.viwath.srulibrarymobile.domain.repository.BorrowRepository
import javax.inject.Inject

class BorrowRepositoryImp @Inject constructor(
    private val api: CoreApi
) : BorrowRepository {

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
}