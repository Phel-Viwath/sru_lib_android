/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * [GetCollegeUseCase]
 *
 * This class represents a use case for retrieving a list of colleges.
 * It interacts with the [CoreRepository] to fetch the college data.
 *
 * This Use Case is responsible for:
 * 1. Loading state: Emitting a `Resource.Loading` state initially.
 * 2. Fetching data: Calling the [CoreRepository.college] function to retrieve a list of [College]
 * 3. Success state: Emitting a `Resource.Success` state containing the retrieved list of colleges.
 * 4. Error handling: Catching any exceptions that occur during the data fetching process and emitting a `Resource.Error` state with the error message.
 * 5. Logging: Logs the retrieved list of colleges for debugging purposes.
 *
 * @property repository The [CoreRepository] instance used to access the college data.
 * @constructor Creates a [GetCollegeUseCase] instance with the provided [CoreRepository].
 */
class GetCollegeUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<Resource<List<College>>> = flow{
        emit(Resource.Loading())
        try {
            val colleges = repository.college()
            Log.d("GetCollegeUseCase", "invoke: $colleges")
            emit(Resource.Success(colleges))
        }catch (e: Exception){
            emit(Resource.Error(e.message.toString()))
        }
    }
}