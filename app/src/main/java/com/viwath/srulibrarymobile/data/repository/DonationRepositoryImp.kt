/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.repository

import com.viwath.srulibrarymobile.data.api.CoreApi
import com.viwath.srulibrarymobile.data.dto.DonationDto
import com.viwath.srulibrarymobile.data.safeCall
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.DonationIO
import com.viwath.srulibrarymobile.domain.repository.DonationRepository
import jakarta.inject.Inject

class DonationRepositoryImp @Inject constructor(
    private val api: CoreApi
) : DonationRepository{
    // donation
    override suspend fun addDonation(donationIO: DonationIO): Result<Unit, DataAppError.Remote> {
        return safeCall { api.addDonation(donationIO) }
    }

    override suspend fun getAllDonation(): Result<List<DonationDto>, DataAppError.Remote> {
        return safeCall { api.getAllDonation() }
    }

    override suspend fun updateDonation(donationIO: DonationIO): Result<Unit, DataAppError.Remote> {
        return safeCall { api.updateDonation(donationIO) }
    }

}