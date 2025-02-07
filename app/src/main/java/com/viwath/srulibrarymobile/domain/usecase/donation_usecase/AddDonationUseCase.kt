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
import com.viwath.srulibrarymobile.domain.model.toDonationDto
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddDonationUseCase @Inject constructor(
    private val repository: CoreRepository
){

    operator fun invoke(donation: Donation): Flow<Resource<Unit>> = flow{
        emit(Resource.Loading())
        val donationDto = donation.toDonationDto()
        when(val result = repository.addDonation(donationDto)){
            is Result.Success -> {
                emit(Resource.Success(result.data))
            }
            is Result.Error -> {
                val message: DataError.Remote = result.error
                val error = handleRemoteError(message)
                emit(Resource.Error(error))
            }
        }
    }

}