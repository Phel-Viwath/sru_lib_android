/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state.book_state

import com.viwath.srulibrarymobile.domain.model.borrow.Borrow

data class BorrowState(
    val isLoading: Boolean = false,
    val borrowList: List<Borrow> = emptyList(),
    val searchKeywordChange: String = "",
    val isFilter: Boolean = false
)