/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.repository.AttendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * `GetRecentEntryUseCase` is a Use Case class responsible for retrieving the most recent entry data.
 *
 * This class encapsulates the logic for fetching the recent entry from the repository
 * and handling potential errors during the process. It uses a Flow to emit the results,
 * allowing for reactive data handling.
 *
 * @property repository The [AttendRepository] instance used to interact with the data source.
 */
class GetRecentEntryUseCase @Inject constructor(
    private val repository: AttendRepository
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