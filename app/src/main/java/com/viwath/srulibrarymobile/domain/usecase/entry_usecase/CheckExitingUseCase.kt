/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.repository.AttendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


/**
 * `CheckExitingUseCase` is a use case class responsible for checking the exiting status of an attendance record.
 *
 * This class interacts with the [AttendRepository] to perform the check and provides the result wrapped
 * in a [Resource] object, allowing for handling of loading, success, and error states.
 *
 * @property repository The [AttendRepository] instance used for data access and checking the exiting status.
 * @constructor Creates a [CheckExitingUseCase] instance with the specified [AttendRepository].
 */
class CheckExitingUseCase @Inject constructor(
    private val repository: AttendRepository
){
    operator fun invoke(id: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.checkExitingAttend(id)
            Log.e("CheckExitingUseCase", "invoke: $result")
            emit(Resource.Success(result))
        }catch (e: Exception){
            Log.e("CheckExitingUseCase", "invoke: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "An error occurred."))
        }
    }
}