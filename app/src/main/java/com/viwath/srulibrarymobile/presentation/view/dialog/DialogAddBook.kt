/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.dialog

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.CollegeId
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.model.LanguageId
import com.viwath.srulibrarymobile.domain.model.book.Book

/**
 * `DialogAddBook` is a utility class designed to manage the data entry and retrieval
 * for adding or editing book information in a UI dialog. It handles user input for
 * various book attributes, dropdown selection for language and college, and the
 * population of fields with existing book data.
 *
 * @param view The root view of the dialog containing the UI elements for book data entry.
 */
class DialogAddBook(view: View) {
    private val edtBookId: TextInputEditText = view.findViewById(R.id.edtBookId)
    private val edtTitle: TextInputEditText = view.findViewById(R.id.edtBookTitle)
    private val edtAuthor: TextInputEditText = view.findViewById(R.id.edtAuthor)
    private val edtGenre: TextInputEditText = view.findViewById(R.id.edtBookGenre)
    private val edtPublicYear: TextInputEditText = view.findViewById(R.id.edtPublicYear)
    private val edtQuan: TextInputEditText = view.findViewById(R.id.edtBookQuan)
    private val spinnerLanguage: Spinner = view.findViewById(R.id.spinnerLanguage)
    private val spinnerCollege: Spinner = view.findViewById(R.id.spinnerCollege)

    private val _languages: MutableList<Language> = mutableListOf()
    private val _college: MutableList<College> = mutableListOf()

    private var collegeId: String = ""
    private var languageId: String = ""



    // Setup dropdown menus for languages and colleges
    fun setupSpinners(context: Context, languages: List<Language>, colleges: List<College>) {
        _languages.clear()
        _college.clear()
        _languages.addAll(languages)
        _college.addAll(colleges)
        val spinnerLayout = android.R.layout.simple_spinner_dropdown_item

        val languageAdapter = ArrayAdapter(context, spinnerLayout, languages.map { it.languageName })
        val collegeAdapter = ArrayAdapter(context, spinnerLayout, colleges.map { it.collegeName })

        spinnerLanguage.adapter = languageAdapter
        spinnerCollege.adapter = collegeAdapter

        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                languageId = languages[position].languageId // Save selected language ID
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerCollege.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                collegeId = colleges[position].collegeId // Save selected college ID
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun getBookData(): BookData{
        val bookId = edtBookId.text.toString().trim()
        val title = edtTitle.text.toString().trim()
        val author = edtAuthor.text.toString().trim()
        val genre = edtGenre.text.toString().trim()
        val year = edtPublicYear.text.toString().toIntOrNull() ?: 0
        val quan = edtQuan.text.toString().toIntOrNull() ?: 0
        return BookData(bookId, title, author, genre, year, quan)
    }

    fun getSelectedIds(): Pair<CollegeId, LanguageId> {
        return Pair(collegeId, languageId)
    }

    @Suppress("UNCHECKED_CAST")
    fun populateBookData(book: Book){
        edtBookId.setText(book.bookId)
        edtTitle.setText(book.bookTitle)
        edtAuthor.setText(book.author ?: "") // Handle nullable author
        edtGenre.setText(book.genre)
        edtPublicYear.setText(book.publicationYear?.toString() ?: "") // Handle nullable publication year
        edtQuan.setText("${book.bookQuan}")
        collegeId = book.collegeId
        languageId = book.languageId

        val languagePosition = (spinnerLanguage.adapter as ArrayAdapter<String>).getPosition(
            _languages.find { it.languageId == book.languageId }?.languageName ?: ""
        )
        spinnerLanguage.setSelection(languagePosition)
        val collegePosition = (spinnerCollege.adapter as ArrayAdapter<String>).getPosition(
            _college.find { it.collegeId == book.collegeId }?.collegeName ?: ""
        )
        spinnerCollege.setSelection(collegePosition)
    }

    fun disableEditText(isEnable: Boolean){
        if (!isEnable){
            edtBookId.isEnabled = false
            edtTitle.isEnabled = false
            edtAuthor.isEnabled = false
            edtGenre.isEnabled = false
            edtPublicYear.isEnabled = false
            edtQuan.isEnabled = false
            spinnerCollege.isEnabled = false
            spinnerLanguage.isEnabled = false
        }
    }


}