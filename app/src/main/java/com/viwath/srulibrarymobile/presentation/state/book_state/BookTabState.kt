/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state.book_state

import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.book.Book
import java.io.File

data class BookTabState(
    val isLoading: Boolean = false,
    val error: String = "",
    //load book
    val books: List<Book> = emptyList(),

    // add book
    val bookId: String = "",
    val bookTitle: String = "",
    val publicYear: Int = 0,
    val bookQuan: Int = 0,
    val author: String = "",
    val genre: String = "",
    val languageId: String = "",
    val collegeId: String = "",
    // upload state
    val file: File? = null,
    // student id for borrow
    val studentId: StudentId = 0,
    // filter
    val genreFilter: Genre = "",
    val searchKeywordChange: String = ""
)


