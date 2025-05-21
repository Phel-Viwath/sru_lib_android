/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.Genre

sealed class InTrashEvent {
    data object RestoreBook: InTrashEvent()
    data object SearchInTrash: InTrashEvent()
    data object DeleteBook: InTrashEvent()
    data object Filter: InTrashEvent()
    data object RefreshData : InTrashEvent()

    data class OnDeleteClicked(val bookId: BookId): InTrashEvent()
    data class OnRestoreClicked(val bookId: BookId): InTrashEvent()
    data class FilterGenreChange(val filter: Genre): InTrashEvent()
    data class SearchTextChange(val keyword: String): InTrashEvent()

}