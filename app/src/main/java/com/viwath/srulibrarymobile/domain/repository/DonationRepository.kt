/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.data.dto.DonationDto
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.DonationIO

interface DonationRepository {
    // donation
    suspend fun addDonation(donationIO: DonationIO): Result<Unit, DataAppError.Remote>
    suspend fun getAllDonation(): Result<List<DonationDto>, DataAppError.Remote>
    suspend fun updateDonation(donationIO: DonationIO): Result<Unit, DataAppError.Remote>
}