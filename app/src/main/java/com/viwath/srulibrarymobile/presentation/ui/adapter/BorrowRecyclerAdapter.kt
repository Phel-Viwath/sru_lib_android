/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.ItemBorrowedBinding
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow
import com.viwath.srulibrarymobile.utils.applyBlur
import com.viwath.srulibrarymobile.utils.getTranslucentColor
import com.viwath.srulibrarymobile.utils.getTransparent

/**
 * [BorrowRecyclerAdapter] is a RecyclerView adapter that displays a list of [Borrow] objects.
 * It handles the display of borrow details, including student ID, book ID, borrow quantity, dates,
 * book title, student name, and return status. It also provides an expandable/collapsible view
 * for detailed information and handles item click events.
 *
 * @param borrow The list of [Borrow] objects to display.
 * @param isDarkMode A boolean indicating whether dark mode is enabled. This affects the appearance
 *                   of the expand/collapse arrow.
 * @param context The application context.
 * @param onItemClicked A lambda function that is invoked when an item in the RecyclerView is clicked.
 *                      It receives the clicked [Borrow] object as a parameter.
 */
class BorrowRecyclerAdapter(
    private val borrow: List<Borrow>,
    private var isClassicMode: Boolean,
    private val isDarkMode: Boolean,
    private val context: Activity,
    private val onItemClicked: (borrow: Borrow) -> Unit
) : RecyclerView.Adapter<BorrowRecyclerAdapter.ViewAdapter>() {

    inner class ViewAdapter(binding: ItemBorrowedBinding) : RecyclerView.ViewHolder(binding.root) {
        // No need to define individual views - they're accessible through binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAdapter {
        val binding = ItemBorrowedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewAdapter(binding)
    }

    override fun getItemCount(): Int = borrow.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewAdapter, position: Int) {
        val borrowList = borrow[position]
        val binding = ItemBorrowedBinding.bind(holder.itemView)

        // set text
        binding.apply {
            tvNo.text = "${position + 1}"
            tvStudentId.text = "Student ID: ${borrowList.studentId}"
            tvBookId.text = "Book ID: ${borrowList.bookId}"

            tvBorrowQuan.text = "Borrow Quan: "
            tvBorrowDate.text = "Borrow Date: "
            tvReturnDate.text = "Return Date: "
            tvBookTitle.text = "Book Title: "
            tvStudentName.text = "Student Name: "
            tvReturned.text = "Returned: "

            tvBorrowQuanValue.text = "${borrowList.bookQuan}"
            tvBorrowDateValue.text = borrowList.borrowDate
            tvReturnDateValue.text = borrowList.giveBackDate
            tvBookTitleValue.text = borrowList.bookTitle
            tvStudentNameValue.text = borrowList.studentName
            tvReturnedValue.text = if (borrowList.isBringBack) "Yes" else "No"
            tvReturnedValue.setTextColor(
                if (borrowList.isBringBack) context.getColor(R.color.primaryTextColor)
                else context.getColor(R.color.red)
            )
        }

        // Sync visibility state with isExpanded
        if (borrowList.isExpanded) {
            TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
            binding.hiddenView.visibility = View.VISIBLE
            binding.arrowButton.setImageResource(
                if (isDarkMode) R.drawable.ic_expand_less_light_24
                else R.drawable.ic_expand_less_dark_24
            )
        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
            binding.hiddenView.visibility = View.GONE
            binding.arrowButton.setImageResource(
                if (isDarkMode) R.drawable.ic_expand_more_light_24
                else R.drawable.ic_expand_more_dark_24
            )
        }

        // action click
        holder.itemView.setOnClickListener {
            onItemClicked(borrowList)
        }
        binding.arrowButton.setOnClickListener {
            val wasExpanded = borrowList.isExpanded
            collapseAllItemsExcept(position)
            borrowList.isExpanded = !wasExpanded
            notifyItemChanged(position)
        }

        if (!isClassicMode){
            binding.root.apply {
                setBackgroundColor(context.getTransparent())
                radius = 5f
            }
            binding.blurViewBorrowed.applyBlur(
                context, 10f, context.getTranslucentColor(isDarkMode)
            )
        }
    }

    private fun collapseAllItemsExcept(position: Int) {
        for (i in borrow.indices) {
            if (i != position && borrow[i].isExpanded) {
                borrow[i].isExpanded = false
                notifyItemChanged(i)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateViewMode(newMode: Boolean) {
        isClassicMode = newMode
        notifyDataSetChanged()
    }
}