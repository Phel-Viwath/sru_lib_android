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
import com.viwath.srulibrarymobile.domain.model.book.toBook
import com.viwath.srulibrarymobile.domain.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Use case for retrieving a list of active books.
 *
 * This class encapsulates the logic for fetching books from the repository,
 * filtering them to only include active books, and transforming them into
 * the desired `Book` domain model. It handles potential errors during the
 * data retrieval process and emits the result as a `Flow` of `Resource` objects.
 *
 * @property repository The repository responsible for data access. Injected via constructor injection.
 */
class GetBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(): Flow<Resource<List<Book>>> = flow {
        emit(Resource.Loading())
        when(val result = repository.getBooks()){
            is Result.Success -> {
                val book = result.data.filter { it.isActive }.map { it.toBook() }
                emit(Resource.Success(book))
            }
            is Result.Error -> {
                val error = result.error
                val message = error.handleRemoteError()
                emit(Resource.Error(message))
            }
        }
    }.flowOn(Dispatchers.IO)
}