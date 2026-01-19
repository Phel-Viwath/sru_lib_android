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
import com.viwath.srulibrarymobile.domain.model.book.Book
import com.viwath.srulibrarymobile.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for adding a new book to the data source.
 *
 * This class handles the logic of adding a book through the repository. It wraps the
 * repository call in a flow and manages the different states of the operation (loading, success, error).
 *
 * @property repository The repository responsible for data access.
 */
class AddBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(bookDto: Book): Flow< Resource<Unit>> = flow {
        emit(Resource.Loading(Unit))
        when(val result = repository.addBooks(listOf(bookDto))){
            is Result.Success -> emit(Resource.Success(Unit))
            is Result.Error -> {
                val error = result.error
                val message = error.handleRemoteError()
                emit(Resource.Error(message))
            }
        }
    }
}
