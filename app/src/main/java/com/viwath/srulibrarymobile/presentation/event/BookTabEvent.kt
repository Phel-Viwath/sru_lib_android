/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

import java.io.File

sealed class BookTabEvent {
    data object SaveBook : BookTabEvent()
    data object Remove : BookTabEvent()
    data object Borrow : BookTabEvent()
    data object UpdateBook: BookTabEvent()
    data object UploadBook: BookTabEvent()

    // add book
    data class BookIdChange(val bookId: String): BookTabEvent()
    data class BookTittleChange(val bookTitle: String): BookTabEvent()
    data class AuthorChange(val author: String? = ""): BookTabEvent()
    data class PublicYearChange(val publicYear: Int): BookTabEvent()
    data class QuanChange(val quan: Int): BookTabEvent()
    data class GenreChange(val genre: String): BookTabEvent()
    data class LanguageChange(val languageId: String): BookTabEvent()
    data class CollegeChange(val collegeId: String): BookTabEvent()
    data class FileChange(val file: File): BookTabEvent()

}