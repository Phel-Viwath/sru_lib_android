/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.repository

import android.util.Log
import com.viwath.srulibrarymobile.data.api.CoreApi
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.data.dto.BookSummary
import com.viwath.srulibrarymobile.data.safeCall
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.model.book.Book
import com.viwath.srulibrarymobile.domain.repository.BookRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class BookRepositoryImp @Inject constructor(
    private val api: CoreApi
) : BookRepository {

    override suspend fun addBooks(books: List<Book>): Result<Unit, DataAppError.Remote> {
        return safeCall { api.addBooks(books) }
    }

    override suspend fun uploadBook(file: MultipartBody.Part): Result<Unit, DataAppError.Remote> {
        return safeCall { api.uploadBook(file) }
    }

    override suspend fun updateBook(book: Book): Result<Unit, DataAppError.Remote> {
        return safeCall { api.updateBook(book) }
    }

    override suspend fun getBooks(): Result<List<BookDto>, DataAppError.Remote> {
        return safeCall{ api.getBooks() }
    }

    override suspend fun getBooksInTrash(): Result<List<BookDto>, DataAppError.Remote> {
        val data =  safeCall { api.bookInTrash() }
        Log.d("CoreRepositoryImp", "getBooksInTrash: $data")
        return data
    }

    override suspend fun getSummaryBook(): Result<BookSummary, DataAppError.Remote> {
        return safeCall { api.getSummaryBook() }
    }

    override suspend fun moveToTrash(bookId: String): Result<Unit, DataAppError.Remote> {
        return safeCall { api.movToTrash(bookId) }
    }

    override suspend fun recoverBook(bookId: String): Result<Unit, DataAppError.Remote> {
        return safeCall { api.recoverBook(bookId) }
    }

    override suspend fun deleteBook(bookId: BookId): Result<Unit, DataAppError.Remote> {
        return safeCall { api.deleteBook(bookId) }
    }

    override suspend fun bookLanguages(): Result<List<Language>, DataAppError.Remote> {
        return safeCall { api.bookLanguage() }
    }

    override suspend fun college(): Result<List<College>, DataAppError.Remote> {
        return safeCall { api.college() }
    }

    override suspend fun searchBook(
        keyword: String
    ): Result<List<BookDto>, DataAppError.Remote> {
        return safeCall { api.searchBook(keyword) }
    }
}