/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * `GetStudentByIDUseCase` is a use case class responsible for retrieving a student's data by their ID.
 *
 * This class encapsulates the business logic for fetching a student from the repository
 * and handling potential errors during the process. It interacts with the [CoreRepository]
 * to access the underlying data source.
 *
 * @property repository The [CoreRepository] instance used to access student data.
 *
 * @constructor Creates a [GetStudentByIDUseCase] instance.
 * @param repository The [CoreRepository] to be injected.
 */
class GetStudentByIDUseCase @Inject constructor(
    private val repository: CoreRepository
){
    operator fun invoke(id: Long): Flow<Resource<Students>> = flow {
        emit(Resource.Loading())
        when(val result = repository.getStudentById(id)){
            is Result.Success -> {
                emit(Resource.Success(result.data))
            }
            is Result.Error -> {
                emit(Resource.Error(result.error.handleRemoteError()))
            }
        }
    }
}