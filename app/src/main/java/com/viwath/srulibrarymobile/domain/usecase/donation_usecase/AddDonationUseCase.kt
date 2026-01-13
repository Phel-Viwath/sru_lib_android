/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.donation_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.DonationIO
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddDonationUseCase @Inject constructor(
    private val repository: CoreRepository
){

    operator fun invoke(donation: DonationIO): Flow<Resource<Unit>> = flow{
        emit(Resource.Loading())
        when(val result = repository.addDonation(donation)){
            is Result.Success -> {
                emit(Resource.Success(result.data))
            }
            is Result.Error -> {
                val error: DataAppError.Remote = result.error
                val message = error.handleRemoteError()
                emit(Resource.Error(message))
            }
        }
    }

}