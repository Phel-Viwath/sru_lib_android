/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state.book_state

import com.viwath.srulibrarymobile.domain.model.Author
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.Donation
import com.viwath.srulibrarymobile.domain.model.DonateDate
import com.viwath.srulibrarymobile.domain.model.DonatorName
import com.viwath.srulibrarymobile.domain.model.DonatorId
import com.viwath.srulibrarymobile.domain.model.BookQuantity
import com.viwath.srulibrarymobile.domain.model.BookTitle
import com.viwath.srulibrarymobile.domain.model.CollegeId
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.LanguageId
import com.viwath.srulibrarymobile.domain.model.PublicationYear

data class DonationState (
    val isLoading: Boolean = false,
    val donationList: List<Donation> = emptyList(),
    val filterGenreChange: String = "",
    val searchChange: String = "",

    val donatorId: DonatorId? = null,
    val donatorName: DonatorName = "",
    val bookId: BookId = "",
    val bookTitle: BookTitle = "",
    val bookQuan: BookQuantity = 0,
    val languageId: LanguageId = "",
    val collegeId: CollegeId = "",
    val author: Author? = null,
    val publicationYear: PublicationYear? = null,
    val genre: Genre = "",
    val donateDate: DonateDate = ""
)