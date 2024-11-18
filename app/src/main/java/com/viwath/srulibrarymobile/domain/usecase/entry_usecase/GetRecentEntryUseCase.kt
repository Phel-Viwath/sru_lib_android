package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import com.viwath.srulibrarymobile.common.result.CoreResult
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecentEntryUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<CoreResult<com.viwath.srulibrarymobile.domain.model.entry.Entry>> = flow {
        try {
            emit(CoreResult.Loading())
            val response = repository.getRecentEntryData()
            emit(CoreResult.Success(response))
        }catch (e: Exception){
            emit(CoreResult.Error(e.message.toString()))
        }
    }
}