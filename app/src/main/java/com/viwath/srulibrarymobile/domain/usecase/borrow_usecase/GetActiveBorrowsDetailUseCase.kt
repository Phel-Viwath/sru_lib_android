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
import com.viwath.srulibrarymobile.domain.repository.BorrowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * `GetActiveBorrowsDetailUseCase` is a use case class responsible for retrieving the details of
 * active borrows from the repository. It handles the business logic for fetching this data,
 * including potential network and other errors, and exposes the result as a Flow of `Resource<List<Borrow>>`.
 *
 * This use case interacts with the `CoreRepository` to fetch the data and transforms it into
 * a list of `Borrow` domain objects before emitting it. It also handles various error scenarios
 * and emits the appropriate `Resource.Error` state.
 *
 * @property repository The [BorrowRepository] instance responsible for data access.
 */
class GetActiveBorrowsDetailUseCase(
    private val repository: BorrowRepository
) {
    operator fun invoke(): Flow<Resource<List<Borrow>>> = flow{
        emit(Resource.Loading())
        when(val result = repository.getActiveBorrowsDetail()){
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