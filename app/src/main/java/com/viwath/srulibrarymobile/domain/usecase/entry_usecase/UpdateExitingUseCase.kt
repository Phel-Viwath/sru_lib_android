package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import com.viwath.srulibrarymobile.common.result.CoreResult
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateExitingUseCase @Inject constructor(
    private val repository: CoreRepository
){
    suspend operator fun invoke(id: Long) : Flow<CoreResult<Boolean>> = flow {
        emit(CoreResult.Loading())
        try {
            val success = repository.updateExitingTime(id)
            emit(CoreResult.Success(success))
        }catch (e: Exception){
            emit(CoreResult.Error(e.message ?: "An error occurred while updating exit time"))
        }
    }
}