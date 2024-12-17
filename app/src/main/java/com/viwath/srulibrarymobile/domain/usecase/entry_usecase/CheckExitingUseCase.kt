package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class CheckExitingUseCase @Inject constructor(
    private val repository: CoreRepository
){
    suspend operator fun invoke(id: String): Flow<Resource<String>> = flow {
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