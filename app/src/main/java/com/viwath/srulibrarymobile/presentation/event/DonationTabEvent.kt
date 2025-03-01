/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BookQuantity
import com.viwath.srulibrarymobile.domain.model.BookTitle
import com.viwath.srulibrarymobile.domain.model.CollegeId
import com.viwath.srulibrarymobile.domain.model.DonateDate
import com.viwath.srulibrarymobile.domain.model.DonatorName
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.LanguageId
import com.viwath.srulibrarymobile.domain.model.Author
import com.viwath.srulibrarymobile.domain.model.DonatorId
import com.viwath.srulibrarymobile.domain.model.PublicationYear

sealed class DonationTabEvent {
    data object OnSubmit: DonationTabEvent()
    data object OnUpdate: DonationTabEvent()
    data object OnFilter: DonationTabEvent()
    data object OnSearch: DonationTabEvent()

    data class OnFilterGenreChange(val filter: String): DonationTabEvent()
    data class OnSearchChange(val search: String): DonationTabEvent()

    data class OnDonatorIdChange(val donatorId: DonatorId): DonationTabEvent()
    data class OnDonatorNameChange(val donatorName: DonatorName): DonationTabEvent()
    data class OnBookIdChange(val bookId: BookId): DonationTabEvent()
    data class OnBookTitleChange(val bookTitle: BookTitle): DonationTabEvent()
    data class OnBookQuantityChange(val bookQuan: BookQuantity): DonationTabEvent()
    data class OnLanguageIdChange(val languageId: LanguageId): DonationTabEvent()
    data class OnCollegeIdChange(val collegeId: CollegeId): DonationTabEvent()
    data class OnAuthorChange(val author: Author?): DonationTabEvent()
    data class OnPublicationYearChange(val publicationYear: PublicationYear?): DonationTabEvent()
    data class OnGenreChange(val genre: Genre): DonationTabEvent()
    data class OnDonateDateChange(val donateDate: DonateDate): DonationTabEvent()

}