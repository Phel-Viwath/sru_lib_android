/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state.book_state

data class SummaryBookState(
    val isLoading: Boolean = false,
    val error: String = "",
    val totalBook: Int = 0,
    val totalDonation: Int = 0,
    val totalBorrowed: Int = 0,
    val totalExpiration: Int = 0,
    val borrowToday: Int = 0,
    val returnToday: Int = 0
)
