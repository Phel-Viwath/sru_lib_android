/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.donation_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.DataError
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.Donation
import com.viwath.srulibrarymobile.domain.model.toDonation
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllDonationUseCase @Inject constructor(
    private val repository: CoreRepository
){
    operator fun invoke(): Flow<Resource<List<Donation>>> = flow{
        emit(Resource.Loading())
        when(val result = repository.getAllDonation()){
            is Result.Success ->{
                val data = result.data.map { it.toDonation() }
                emit(Resource.Success(data))
            }
            is Result.Error -> {
                val error: DataError.Remote = result.error
                val message = error.handleRemoteError()
                emit(Resource.Error(message))
            }
        }
    }
}