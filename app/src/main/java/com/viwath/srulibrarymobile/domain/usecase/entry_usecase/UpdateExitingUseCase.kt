/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * `UpdateExitingUseCase` is a Use Case class responsible for updating the exiting time
 * associated with a specific record identified by its ID.
 *
 * This class encapsulates the business logic of updating the exiting time and handles
 * potential errors that may occur during the process. It interacts with the
 * `CoreRepository` to perform the actual update operation.
 *
 * @property repository The `CoreRepository` instance used to interact with the data layer.
 *                     It's injected via constructor injection.
 */
class UpdateExitingUseCase @Inject constructor(
    private val repository: CoreRepository
){
    operator fun invoke(id: Long) : Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val success = repository.updateExitingTime(id)
            emit(Resource.Success(success))
        }catch (e: Exception){
            emit(Resource.Error(e.message ?: "An error occurred while updating exit time"))
        }
    }
}