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
import com.viwath.srulibrarymobile.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for searching books based on a keyword.
 *
 * This class encapsulates the logic for searching books in the data source
 * through the [DashboardRepository]. It handles the flow of data, including
 * loading, success, and error states. It also manages potential exceptions
 * during the search process, such as network errors or HTTP errors.
 *
 * @property repository The [DashboardRepository] instance responsible for interacting with the data source.
 */
class SearchBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(keyword: String): Flow<Resource<List<Book>>> = flow{
        emit(Resource.Loading())
        when(val result = repository.searchBook(keyword)){
            is Result.Success -> emit(Resource.Success(result.data.map { it.toBook() }))
            is Result.Error -> emit(Resource.Error(result.error.handleRemoteError()))
        }
    }
}