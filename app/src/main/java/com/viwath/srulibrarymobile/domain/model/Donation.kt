/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model

import com.viwath.srulibrarymobile.data.dto.DonationDto

data class Donation(
    val donatorName: DonatorName,
    val bookId: BookId,
    val bookTitle: BookTitle,
    val bookQuan: BookQuantity,
    val languageName: LanguageName,
    val collegeName: CollegeName,
    val author: String?,
    val publicationYear: PublicationYear?,
    val genre: Genre,
    val donateDate: DonateDate
)

fun Donation.toDonationDto(): DonationDto = DonationDto(
    donatorId = null,
    donatorName = donatorName,
    bookId = bookId,
    bookTitle = bookTitle,
    bookQuan = bookQuan,
    languageName = languageName,
    collegeName = collegeName,
    author = author,
    publicationYear = publicationYear,
    genre = genre,
    donateDate = donateDate
)