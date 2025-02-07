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

class UpdateDonationUseCase @Inject constructor(
    private val repository: CoreRepository
){
    operator fun invoke(donationDto: DonationDto): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        when(val result = repository.updateDonation(donationDto)){
            is Result.Success -> {
                emit(Resource.Success(result.data))
            }
            is Result.Error -> {
                val error: DataError.Remote = result.error
                val message = handleRemoteError(error)
                emit(Resource.Error(message))
            }
        }
    }
}