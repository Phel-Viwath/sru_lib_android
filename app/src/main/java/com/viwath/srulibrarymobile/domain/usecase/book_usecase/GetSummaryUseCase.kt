/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.repository.BookRepository
import com.viwath.srulibrarymobile.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * `GetSummaryUseCase` is a Use Case class responsible for fetching a summary of a book.
 * It interacts with the [DashboardRepository] to retrieve the summary data and
 * emits the result as a [Flow] of [Resource] objects.
 *
 * This class follows the Clean Architecture principles by encapsulating the business logic
 * related to fetching the book summary, separating it from the data layer and the UI layer.
 *
 * @property repository The [DashboardRepository] instance used to access the data layer and
 *                    fetch the book summary. Injected via constructor injection.
 */
class GetSummaryUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(): Flow<Resource<BookSummary>> = flow {
        emit(Resource.Loading())
        when(val result = repository.getSummaryBook()){
            is Result.Success -> {
                val summary = result.data
                emit(Resource.Success(summary))
            }
            is Result.Error -> emit(Resource.Error(result.error.handleRemoteError()))
        }
    }
}