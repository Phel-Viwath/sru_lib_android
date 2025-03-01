/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * `RecoverBookUseCase` is a use case class responsible for recovering a book.
 *
 * This class encapsulates the business logic for recovering a book, interacting with the
 * underlying repository to perform the operation. It handles potential exceptions that may
 * occur during the process, such as network issues or HTTP errors, and returns the result
 * wrapped in a `Resource` object.
 *
 * @property repository The `CoreRepository` instance used to interact with the data layer.
 * @constructor Creates a `RecoverBookUseCase` with the provided `CoreRepository`.
 */
class RecoverBookUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(bookId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        when(val result = repository.recoverBook(bookId)){
            is Result.Success -> {
                emit(Resource.Success(true))
            }
            is Result.Error -> {
                emit(Resource.Error(result.error.handleRemoteError()))
            }
        }
    }
}