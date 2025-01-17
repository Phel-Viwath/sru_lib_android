/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.dto

import com.viwath.srulibrarymobile.domain.model.Author
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BookQuantity
import com.viwath.srulibrarymobile.domain.model.BookTitle
import com.viwath.srulibrarymobile.domain.model.CollegeId
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.InActiveDate
import com.viwath.srulibrarymobile.domain.model.IsActive
import com.viwath.srulibrarymobile.domain.model.LanguageId
import com.viwath.srulibrarymobile.domain.model.PublicationYear
import com.viwath.srulibrarymobile.domain.model.ReceiveDate

data class BookDto(
    val bookId: BookId,
    val bookTitle: BookTitle,
    val bookQuan: BookQuantity,
    val languageId: LanguageId,
    val collegeId: CollegeId,
    val author: Author?,
    val publicationYear: PublicationYear?,
    val genre: Genre,
    val receiveDate: ReceiveDate?,
    val isActive: IsActive,
    val inactiveDate: InActiveDate
)