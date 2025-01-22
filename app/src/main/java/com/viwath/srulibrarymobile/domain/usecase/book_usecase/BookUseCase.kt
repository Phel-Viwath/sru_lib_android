/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.GetStudentByIDUseCase

data class BookUseCase(
    val addBookUseCase: AddBookUseCase,
    val getBooksUseCase: GetBooksUseCase,
    val updateBookUseCase: UpdateBookUseCase,
    val removeBookUseCase: RemoveBookUseCase,
    val recoverBookUseCase: RecoverBookUseCase,
    val getBookInTrashUseCase: GetBookInTrashUseCase,
    val getSummaryUseCase: GetSummaryUseCase,
    val getLanguageUseCase: GetLanguageUseCase,
    val getCollegeUseCase: GetCollegeUseCase,
    val uploadBookUseCase: UploadBookUseCase,
    val getStudentByIDUseCase: GetStudentByIDUseCase,
    val searchBookUseCase: SearchBookUseCase
)
