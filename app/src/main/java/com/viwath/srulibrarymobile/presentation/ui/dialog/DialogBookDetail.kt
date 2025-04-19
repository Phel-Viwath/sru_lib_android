/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.dialog

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.book.Book

/**
 * DialogBookDetail is a class responsible for populating and displaying detailed information
 * about a book within a dialog or a similar UI component. It takes a View as input, which
 * is assumed to contain the TextViews used to display the book's attributes.
 */
class DialogBookDetail(view: View) {
    private val tvBookId: TextView = view.findViewById(R.id.tvId)
    private val tvBookTitle: TextView = view.findViewById(R.id.tvTitle)
    private val tvBookQuan: TextView = view.findViewById(R.id.tvQuan)
    private val tvLanguage: TextView = view.findViewById(R.id.tvLanguage)
    private val tvCollege: TextView = view.findViewById(R.id.tvCollege)
    private val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
    private val tvPublicYear: TextView = view.findViewById(R.id.tvPublicYear)
    private val tvGenre: TextView = view.findViewById(R.id.tvGenre)
    private val tvReceiveDate: TextView = view.findViewById(R.id.tvReceiveDate)

    @SuppressLint("SetTextI18n")
    fun populateData(book: Book){
        tvBookId.text = "Book ID: ${book.bookId}"
        tvBookTitle.text = "Book Title: ${book.bookTitle}"
        tvBookQuan.text = "Book Quan: ${book.bookQuan}"
        tvLanguage.text = "Language: ${if (book.languageId.lowercase() == "kh") "Khmer" else "English"}"
        tvCollege.text = "College ID: ${book.collegeId}"
        tvAuthor.text = "Author: ${book.author ?: "N/A"}"
        tvPublicYear.text = "Publication Year: " + book.publicationYear
        tvGenre.text = "Genre: ${book.genre}"
        tvReceiveDate.text = "Receive Date: ${book.receiveDate ?: "N/A"}"
    }
}