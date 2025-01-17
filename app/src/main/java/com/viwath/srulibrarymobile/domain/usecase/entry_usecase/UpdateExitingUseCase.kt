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