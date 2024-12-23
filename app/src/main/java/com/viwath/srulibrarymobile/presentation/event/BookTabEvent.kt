package com.viwath.srulibrarymobile.presentation.event

import com.viwath.srulibrarymobile.data.dto.BookDto

sealed class BookTabEvent {
    data class SaveBook(val book: List<BookDto>) : BookTabEvent()
    data class Remove(val bookId: String) : BookTabEvent()
    data class Borrow(val studentId: String, val bookId: String) : BookTabEvent()
    data class UpdateBook(val book: BookDto) : BookTabEvent()
}