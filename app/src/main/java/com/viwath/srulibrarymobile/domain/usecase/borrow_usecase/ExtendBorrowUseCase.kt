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
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * `ExtendBorrowUseCase` is a use case responsible for extending the duration of a borrowed item.
 *
 * It interacts with the [CoreRepository] to perform the extension operation.
 *
 * @property repository The [CoreRepository] instance used to communicate with the data layer.
 */
class ExtendBorrowUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(id: BorrowId): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        when(val result = repository.extendBorrow(id)){
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