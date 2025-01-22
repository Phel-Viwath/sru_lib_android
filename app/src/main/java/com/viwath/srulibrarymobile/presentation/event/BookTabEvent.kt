/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.StudentId
import java.io.File

sealed class BookTabEvent {
    data object SaveBook : BookTabEvent()
    data object Remove : BookTabEvent()
    data object Borrow : BookTabEvent()
    data object UpdateBook: BookTabEvent()
    data object UploadBook: BookTabEvent()
    data object GetStudent: BookTabEvent()
    data object FilterGenre: BookTabEvent()
    data object Search: BookTabEvent()
    data object FilterByIdOrTitle: BookTabEvent()

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

    data class StudentIdChange(val studentId: StudentId): BookTabEvent()

    data class FilterGenreChange(val filter: Genre): BookTabEvent()
    data class SearchChange(val search: String): BookTabEvent()
}