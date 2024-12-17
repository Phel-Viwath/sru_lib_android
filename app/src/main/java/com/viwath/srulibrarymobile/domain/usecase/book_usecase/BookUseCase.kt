package com.viwath.srulibrarymobile.domain.usecase.book_usecase

data class BookUseCase(
    val addBookUseCase: AddBookUseCase,
    val getBooksUseCase: GetBooksUseCase,
    val updateBookUseCase: UpdateBookUseCase,
    val removeBookUseCase: RemoveBookUseCase,
    val recoverBookUseCase: RecoverBookUseCase,
    val getBookInTrashUseCase: GetBookInTrashUseCase,
    val getSummaryUseCase: GetSummaryUseCase
)
