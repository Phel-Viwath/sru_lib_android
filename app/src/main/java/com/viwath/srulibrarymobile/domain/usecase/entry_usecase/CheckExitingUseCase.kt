package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.CoreResult
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class CheckExitingUseCase @Inject constructor(
    private val repository: CoreRepository
){
    suspend operator fun invoke(id: String): Flow<CoreResult<String>> = flow {
        emit(CoreResult.Loading())
        try {
            val result = repository.checkExitingAttend(id)
            Log.e("CheckExitingUseCase", "invoke: $result")
            emit(CoreResult.Success(result))
        }catch (e: Exception){
            Log.e("CheckExitingUseCase", "invoke: ${e.message}", e)
            emit(CoreResult.Error(e.localizedMessage ?: "An error occurred."))
        }
    }
}