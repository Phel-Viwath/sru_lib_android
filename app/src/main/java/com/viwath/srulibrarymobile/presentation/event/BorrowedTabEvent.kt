/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.model.StudentId

sealed class BorrowedTabEvent {
    data object FilterOverDue: BorrowedTabEvent()
    data object SearchBorrow: BorrowedTabEvent()
    data object ExtendBorrow: BorrowedTabEvent()
    data object FilterByTextSearch: BorrowedTabEvent()
    data object GetAllBorrow: BorrowedTabEvent()
    data object ReturnBook: BorrowedTabEvent()

    //
    data class OnSearchTextChange(val keyword: String): BorrowedTabEvent()
    data class OnFilterChange(val isFilter: Boolean): BorrowedTabEvent()
    data class OnBorrowIdChange(val borrowId: BorrowId): BorrowedTabEvent()
    data class OnReturnBookDataChange(val studentId: StudentId, val bookId: BookId): BorrowedTabEvent()
}