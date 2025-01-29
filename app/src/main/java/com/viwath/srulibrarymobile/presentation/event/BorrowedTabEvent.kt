/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

sealed class BorrowedTabEvent {
    data object FilterOverDue: BorrowedTabEvent()
    data object SearchBorrow: BorrowedTabEvent()
    data object ExtendBorrow: BorrowedTabEvent()

    //
    data class OnSearchTextChange(val keyword: String): BorrowedTabEvent()
    data class OnFilterChange(val isFilter: Boolean): BorrowedTabEvent()
}