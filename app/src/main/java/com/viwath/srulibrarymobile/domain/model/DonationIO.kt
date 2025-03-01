/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model

data class DonationIO(
    val donatorId: DonatorId?,
    val donatorName: DonatorName,
    val bookId: BookId,
    val bookTitle: BookTitle,
    val bookQuan: BookQuantity,
    val languageId: LanguageId,
    val collegeId: CollegeId,
    val author: Author?,
    val publicationYear: PublicationYear?,
    val genre: Genre,
    val donateDate: DonateDate
)
