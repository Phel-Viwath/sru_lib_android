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
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow
import com.viwath.srulibrarymobile.domain.model.borrow.toBorrow
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for searching borrows based on a keyword.
 *
 * This class encapsulates the logic for searching borrows from the data source
 * via the [CoreRepository]. It handles loading states, success responses, and
 * various error conditions (like network issues and HTTP errors).
 *
 * @property repository The [CoreRepository] instance used to interact with the data source.
 */
class SearchBorrowUseCase @Inject constructor(
    private val repository: CoreRepository
){
    operator fun invoke(keyword: String): Flow<Resource<List<Borrow>>> = flow{
        emit(Resource.Loading())
        when(val result = repository.searchBorrow(keyword)){
            is Result.Success -> {
                val data = result.data.map { it.toBorrow() }
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