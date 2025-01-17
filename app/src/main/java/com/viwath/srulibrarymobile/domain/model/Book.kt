/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model

import com.viwath.srulibrarymobile.data.dto.BookDto

data class Book(
    val bookId: BookId,
    val bookTitle: BookTitle,
    val bookQuan: BookQuantity,
    val languageId: LanguageId,
    val collegeId: CollegeId,
    val author: Author?,
    val publicationYear: PublicationYear?,
    val genre: Genre,
    val receiveDate: ReceiveDate?
)

fun BookDto.toBook() = Book(
    bookId = bookId,
    bookTitle = bookTitle,
    bookQuan = bookQuan,
    languageId = languageId,
    collegeId = collegeId,
    author = author,
    publicationYear = publicationYear,
    genre = genre,
    receiveDate = receiveDate
)
