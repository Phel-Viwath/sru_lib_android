/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.dto

data class BookDto(
    val bookId: String,
    val bookTitle: String,
    val bookQuan: Int,
    val languageId: String,
    val collegeId: String,
    val author: String?,
    val publicationYear: Int?,
    val genre: String,
    val receiveDate: String?
)