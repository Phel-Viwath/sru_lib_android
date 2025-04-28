/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

import com.viwath.srulibrarymobile.domain.model.BookId

sealed class InTrashEvent {
    data object RestoreBook: InTrashEvent()
    data object SearchInTrash: InTrashEvent()
    data object DeleteBook: InTrashEvent()
    data object FilterByTextSearch: InTrashEvent()
    data object RefreshData : InTrashEvent()

    data class OnDeleteClicked(val bookId: BookId): InTrashEvent()
    data class OnRestoreClicked(val bookId: BookId): InTrashEvent()

}