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
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for returning a book.
 *
 * This class encapsulates the logic for returning a book to the library. It interacts with the
 * [CoreRepository] to perform the actual return operation and handles potential errors, such as
 * network issues or server errors.
 *
 * @property repository The [CoreRepository] instance responsible for interacting with the data layer.
 */
class ReturnBookUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(studentId: StudentId, bookId: BookId): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        when(val result = repository.returnBook(studentId, bookId)){
            is Result.Success -> {
                val data = result.data
                emit(Resource.Success(data))
            }
            is Result.Error -> {
                val error = result.error
                val message = error.handleRemoteError()
                emit(Resource.Error(message))
            }
        }
    }
}