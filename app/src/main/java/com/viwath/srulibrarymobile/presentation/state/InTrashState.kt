/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state

import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.book.BookInTrash

data class InTrashState(
    val booksInTrash: List<BookInTrash> = emptyList(),
    val isLoading: Boolean = false,
    val bookId: BookId = ""
)
