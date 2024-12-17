package com.viwath.srulibrarymobile.presentation.state.book_state

import com.viwath.srulibrarymobile.data.dto.BookDto

sealed class TrashState {
    data object Loading: TrashState()
    data class Error(val errorMsg: String = ""): TrashState()
    data class DisplayTrashBooks(val trashBooks: List<BookDto>) : TrashState()
    data object RecoverBookSuccess : TrashState()
}