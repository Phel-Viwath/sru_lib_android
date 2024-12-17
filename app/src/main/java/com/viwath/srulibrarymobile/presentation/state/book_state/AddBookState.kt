package com.viwath.srulibrarymobile.presentation.state.book_state

import com.viwath.srulibrarymobile.data.dto.BookDto

sealed class AddBookState {
    data class DisplayBooks(val books: List<BookDto> = emptyList()): AddBookState()
    data object AddBookSuccess: AddBookState()
    data object RemoveSuccess: AddBookState()
    data object Loading: AddBookState()
    data class Error(val errorMsg: String = ""): AddBookState()
}