/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.dto

data class BookSummary(
    val todayBorrowed: Int,
    val todayReturned: Int,
    val totalBook: Int,
    val totalBorrow: Int,
    val totalDonation: Int,
    val totalExp: Int
)