/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state.book_state

import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow

data class BorrowState(
    val isLoading: Boolean = false,
    val borrowList: List<Borrow> = emptyList(),
    val searchKeywordChange: String = "",
    val isFilter: Boolean = false,

    val bookId: BookId = "",
    val borrowId: BorrowId = 0,
    val studentId: StudentId = 0L
)