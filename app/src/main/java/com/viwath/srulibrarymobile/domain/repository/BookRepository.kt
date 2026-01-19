/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.model.book.Book
import okhttp3.MultipartBody

interface BookRepository {
    // book
    suspend fun addBooks(books: List<Book>): Result<Unit, DataAppError.Remote>
    suspend fun uploadBook(file: MultipartBody.Part):Result<Unit, DataAppError.Remote>
    suspend fun updateBook(book: Book): Result<Unit, DataAppError.Remote>
    suspend fun getBooks(): Result<List<BookDto>, DataAppError.Remote>
    suspend fun getBooksInTrash(): Result<List<BookDto>, DataAppError.Remote>
    suspend fun getSummaryBook():Result<BookSummary, DataAppError.Remote>
    suspend fun moveToTrash(bookId: BookId): Result<Unit, DataAppError.Remote>
    suspend fun recoverBook(bookId: BookId): Result<Unit, DataAppError.Remote>
    suspend fun bookLanguages(): Result<List<Language>, DataAppError.Remote>
    suspend fun college(): Result<List<College>, DataAppError.Remote>
    suspend fun searchBook(keyword: String): Result<List<BookDto>, DataAppError.Remote>
    suspend fun deleteBook(bookId: BookId): Result<Unit, DataAppError.Remote>

}