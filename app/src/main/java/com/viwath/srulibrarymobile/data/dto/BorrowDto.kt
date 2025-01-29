/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.dto

data class BorrowDto(
    val borrowId: Long?,
    val bookId: String,
    val bookQuan: Int,
    val studentId: Long,
    val borrowDate: String,
    val giveBackDate: String,
    val isBringBack: Boolean,
    val isExtend: Boolean
)
