/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.donation_usecase

data class DonationUseCase(
    val getAllDonationUseCase: GetAllDonationUseCase,
    val addDonationUseCase: AddDonationUseCase,
    val updateDonationUseCase: UpdateDonationUseCase
)