/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.donation_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.data.dto.DonationDto
import com.viwath.srulibrarymobile.domain.DataError
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllDonationUseCase @Inject constructor(
    private val repository: CoreRepository
){
    operator fun invoke(): Flow<Resource<List<DonationDto>>> = flow{
        emit(Resource.Loading())
        when(val result = repository.getAllDonation()){
            is Result.Success ->{
                val data = result.data
                emit(Resource.Success(data))
            }
            is Result.Error -> {
                val message: DataError.Remote = result.error
                val error = handleRemoteError(message)
                emit(Resource.Error(error))
            }
        }
    }
}