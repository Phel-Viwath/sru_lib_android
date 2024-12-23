package com.viwath.srulibrarymobile.presentation.state.book_state

import com.viwath.srulibrarymobile.data.dto.BookDto

data class BookTabState(
    val isLoading: Boolean = false,
    val books: List<BookDto> = emptyList(),
    val error: String = ""
)


