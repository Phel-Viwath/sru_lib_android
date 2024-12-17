package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecentEntryUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<Resource<com.viwath.srulibrarymobile.domain.model.entry.Entry>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getRecentEntryData()
            emit(Resource.Success(response))
        }catch (e: Exception){
            emit(Resource.Error(e.message.toString()))
        }
    }
}