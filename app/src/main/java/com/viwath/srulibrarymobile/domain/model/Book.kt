/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model

import com.viwath.srulibrarymobile.data.dto.BookDto

data class Book(
    val bookId: String,
    val bookTitle: String,
    val bookQuan: Int,
    val author: String?,
    val publicationYear: Int?,
    val genre: String,
    val receiveDate: String?
)

fun BookDto.toBook() = Book(
    bookId = bookId,
    bookTitle = bookTitle,
    bookQuan = bookQuan,
    author = author,
    publicationYear = publicationYear,
    genre = genre,
    receiveDate = receiveDate
)
