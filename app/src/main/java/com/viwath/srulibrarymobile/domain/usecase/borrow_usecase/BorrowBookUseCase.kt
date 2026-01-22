/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.borrow_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.borrow.BorrowRequest
import com.viwath.srulibrarymobile.domain.repository.BorrowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * A use case class responsible for handling the borrowing of a book.
 *
 * This class interacts with the [BorrowRepository] to perform the actual
 * borrowing operation and handles various scenarios, including success,
 * failure, network issues, and HTTP errors. It emits a [Resource] state
 * representing the outcome of the borrow operation.
 *
 * @property repository The [BorrowRepository] instance used to communicate with
 *                     the data layer for borrowing operations.
 */
class BorrowBookUseCase @Inject constructor(
    private val repository: BorrowRepository
) {
    operator fun invoke(
        borrowRequest: BorrowRequest
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when(val result = repository.borrowBook(borrowRequest)){
            is Result.Success -> {
                emit(Resource.Success("Borrow request successfully"))
            }
            is Result.Error -> {
                val error = result.error
                val message = error.handleRemoteError()
                emit(Resource.Error(message))
            }
        }
    }
}