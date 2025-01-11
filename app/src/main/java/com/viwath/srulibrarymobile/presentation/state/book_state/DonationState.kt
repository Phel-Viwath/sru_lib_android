/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state.book_state

import com.viwath.srulibrarymobile.data.dto.BookDto

sealed class DonationState {
    data class DisplayDonation(val books: List<BookDto> = emptyList()): DonationState()
    data object AddDonationSuccess: DonationState()
    data object RemoveSuccess: DonationState()
    data object Loading: DonationState()
    data class Error(val errorMsg: String = ""): DonationState()

}