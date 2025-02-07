/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.DialogExtendBinding
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow
import com.viwath.srulibrarymobile.utils.DateTimeUtil.extendBorrowDate

/**
 * This class represents a dialog that allows the user to either extend or return a borrowed item.
 * It handles UI interactions related to the dialog, such as theme changes, radio button selection,
 * data population, and button click events.
 */
@SuppressLint("SetTextI18n")
class DialogExtendOrReturn(
    view: View
) {
    private val binding =  DialogExtendBinding.bind(view)

    fun themeChange(isDarkMode: Boolean, context: Context){
        binding.btExitDialog.setImageResource(if (isDarkMode) R.drawable.ic_close_light_24 else R.drawable.ic_close_dark_24)
        val colorList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.light_green))
        binding.radioExtend.buttonTintList = colorList
        binding.radioReturn.buttonTintList = colorList
    }

    fun onRadioChange(listener: (Boolean) -> Unit) {
        binding.radioGroup.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioExtend -> listener(false)
                R.id.radioReturn -> listener(true)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun setVisibility(isReturn: Boolean) {
        if (isReturn){
            binding.tvNewReturnDate.visibility = View.GONE
            binding.tvNewReturnDateValue.visibility = View.GONE
            binding.btReturnOrExtend.text = "Return"
        }
        else{
            binding.tvNewReturnDate.visibility = View.VISIBLE
            binding.tvNewReturnDateValue.visibility = View.VISIBLE
            binding.btReturnOrExtend.text = "Extend"
        }
    }

    @Suppress("SetTextI18n")
    fun populateReturnData(borrow: Borrow){
        binding.tvBookId.text = "Book ID: "
        binding.tvBookTitle.text = "Book Title: "
        binding.tvStudentId.text = "Student ID: "
        binding.tvStudentName.text = "Student Name: "
        binding.tvBorrowDate.text = "Borrow Date: "
        binding.tvReturnDate.text = "Return Date: "
        binding.tvNewReturnDate.text = "New Return Date: "
        //
        binding.tvBookIdValue.text = borrow.bookId
        binding.tvBookTitleValue.text = borrow.bookTitle
        binding.tvStudentIdValue.text = borrow.studentId.toString()
        binding.tvStudentNameValue.text = borrow.studentName
        binding.tvBorrowDateValue.text = borrow.borrowDate
        binding.tvReturnDateValue.text = borrow.giveBackDate

        val extendDate = borrow.giveBackDate.extendBorrowDate()
        binding.tvNewReturnDateValue.text = extendDate

    }

    fun onExitClick(listener: () -> Unit) {
        binding.btExitDialog.setOnClickListener { listener() }
    }

    fun onReturnOrExtendClick(borrow: Borrow,onReturnOrExtendClick: (StudentId?, BookId?, BorrowId?) -> Unit) {
        binding.btReturnOrExtend.setOnClickListener {
            Log.d("DialogExtend", "onReturnOrExtendClick: Clicked")
            if (binding.radioReturn.isChecked){
                onReturnOrExtendClick(borrow.studentId, borrow.bookId, null)
            }
            if (binding.radioExtend.isChecked){
                onReturnOrExtendClick(null, null, borrow.borrowId)
            }
        }
    }


}