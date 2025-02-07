/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.dto

import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BookQuantity
import com.viwath.srulibrarymobile.domain.model.BookTitle
import com.viwath.srulibrarymobile.domain.model.CollegeName
import com.viwath.srulibrarymobile.domain.model.DonateDate
import com.viwath.srulibrarymobile.domain.model.DonatorId
import com.viwath.srulibrarymobile.domain.model.DonatorName
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.LanguageName
import com.viwath.srulibrarymobile.domain.model.PublicationYear

data class DonationDto(
    val donatorId: DonatorId?,
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
