/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.dialog

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.Book
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BookQuantity
import com.viwath.srulibrarymobile.domain.model.StudentId

class DialogBorrow(view: View) {
    var book: Book? = null

    val tilStudentId: TextInputLayout = view.findViewById(R.id.tilStudentId)
    val edtStudentId: TextInputEditText = view.findViewById(R.id.edtStudentId)
    val edtQuan: TextInputEditText = view.findViewById(R.id.edtBookQuan)
    val tvId: TextView = view.findViewById(R.id.edtBookId)
    val tvBookTitle: TextView = view.findViewById(R.id.edtBookTitle)
    val tvAvailableQuan: TextView = view.findViewById(R.id.edtAvailableQuan)
    val btBorrow: MaterialButton = view.findViewById(R.id.btBorrow)

    @SuppressLint("SetTextI18n")
    fun populateBookData(book: Book) {
        this.book = book
        tvId.text = book.bookId
        tvBookTitle.text = book.bookTitle
        tvAvailableQuan.text = "${book.bookQuan}"
    }

    fun onSearchClick(listener: (StudentId) -> Unit) {
        tilStudentId.setEndIconOnClickListener {
            val studentId = edtStudentId.text.toString().trim().toLongOrNull()
            if (studentId == null){
                edtStudentId.error = "Please enter student ID"
                return@setEndIconOnClickListener
            }
            if (studentId <= 0L){
                edtStudentId.error = "Please enter student ID"
                return@setEndIconOnClickListener
            }
            edtStudentId.error = null
            listener(studentId)
        }
    }

    fun onBorrowClick(listener: (StudentId, BookQuantity, BookId) -> Unit) {
        btBorrow.setOnClickListener {
            val studentId = edtStudentId.text.toString().trim()
            val borrowQuantity = edtQuan.text.toString().trim().toIntOrNull() ?: 0
            val bookId = book?.bookId ?: return@setOnClickListener
            Log.d("ModalBorrow", "onBorrowClick: $bookId")
            if (studentId.isEmpty() || borrowQuantity <= 0){
                edtStudentId.error = "Please enter student ID"
                edtQuan.error = "Please enter borrow quantity"
                return@setOnClickListener
            }
            if (borrowQuantity > 2){
                edtQuan.error = "Maximum borrow quantity is 2"
                return@setOnClickListener
            }
            edtStudentId.error = null
            edtQuan.error = null
            Log.d("ModalBorrow", "onBorrowClick: OnBorrowClick")
            listener(studentId.toLong(), borrowQuantity, bookId)
        }
    }
}