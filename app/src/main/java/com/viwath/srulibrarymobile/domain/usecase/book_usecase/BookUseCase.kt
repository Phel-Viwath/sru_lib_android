/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.GetStudentByIDUseCase

/**
 * `BookUseCase` is a data class that encapsulates all the use cases related to books.
 * It provides a centralized way to access various book-related operations, such as adding,
 * retrieving, updating, removing, and searching books, as well as managing books in trash,
 * getting summaries, managing language and college information, uploading books, and fetching student information.
 *
 * This class acts as a facade, simplifying the interaction with the different book-related functionalities.
 *
 * @property addBookUseCase Use case for adding a new book.
 * @property getBooksUseCase Use case for retrieving a list of all books.
 * @property updateBookUseCase Use case for updating an existing book's information.
 * @property removeBookUseCase Use case for moving a book to the trash/removing it.
 * @property recoverBookUseCase Use case for recovering a book from the trash.
 * @property getBookInTrashUseCase Use case for retrieving a list of books currently in the trash.
 * @property getSummaryUseCase Use case for retrieving a summary related to book data.
 * @property getLanguageUseCase Use case for retrieving language related data.
 * @property getCollegeUseCase Use case for retrieving college-related data.
 * @property uploadBookUseCase Use case for uploading a book.
 * @property getStudentByIDUseCase Use case for retrieving a student's information by their ID.
 * @property searchBookUseCase Use case for searching books based on specific criteria.
 */
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
