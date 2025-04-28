/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.book

import com.viwath.srulibrarymobile.domain.model.Author
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BookQuantity
import com.viwath.srulibrarymobile.domain.model.BookTitle
import com.viwath.srulibrarymobile.domain.model.CollegeId
import com.viwath.srulibrarymobile.domain.model.CollegeName
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.LanguageId
import com.viwath.srulibrarymobile.domain.model.PublicationYear
import com.viwath.srulibrarymobile.domain.model.ReceiveDate

data class BookInTrash(
    val bookId: BookId,
    val bookTitle: BookTitle,
    val bookQuan: BookQuantity,
    val collegeName: CollegeName,
    val author: Author?,
    val publicationYear: PublicationYear?,
    val genre: Genre,
    var isExpended: Boolean = false
)