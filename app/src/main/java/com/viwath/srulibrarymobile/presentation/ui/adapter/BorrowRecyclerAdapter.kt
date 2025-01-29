/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.card.MaterialCardView
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow

class BorrowRecyclerAdapter(
    private val borrow: List<Borrow>,
    private val isDarkMode: Boolean,
    private val context: Context
) : RecyclerView.Adapter<BorrowRecyclerAdapter.ViewAdapter>(){

    inner class ViewAdapter(view: View): RecyclerView.ViewHolder(view){
        val tvNo = view.findViewById<TextView>(R.id.tvNo)
        val tvStudentId = view.findViewById<TextView>(R.id.tvStudentId)
        val tvBookId = view.findViewById<TextView>(R.id.tvBookId)

        ///
        val tvBorrowQuan = view.findViewById<TextView>(R.id.tvBorrowQuan)
        val tvBorrowDate = view.findViewById<TextView>(R.id.tvBorrowDate)
        val tvReturnDate = view.findViewById<TextView>(R.id.tvReturnDate)
        val tvBookTitle = view.findViewById<TextView>(R.id.tvBookTitle)
        val tvStudentName = view.findViewById<TextView>(R.id.tvStudentName)
        val tvReturned = view.findViewById<TextView>(R.id.tvReturned)

        ///
        val tvBookTitleValue = view.findViewById<TextView>(R.id.tvBookTitleValue)
        val tvBorrowDateValue = view.findViewById<TextView>(R.id.tvBorrowDateValue)
        val tvReturnDateValue = view.findViewById<TextView>(R.id.tvReturnDateValue)

        val tvStudentNameValue = view.findViewById<TextView>(R.id.tvStudentNameValue)
        val tvBorrowQuanValue = view.findViewById<TextView>(R.id.tvBorrowQuanValue)
        val tvReturnedValue = view.findViewById<TextView>(R.id.tvReturnedValue)

        val arrowButton = view.findViewById<ImageButton>(R.id.arrow_button)
        val hiddenView = view.findViewById<LinearLayout>(R.id.hidden_view)
        val cardView = view.findViewById<MaterialCardView>(R.id.base_cardview)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAdapter {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_borrowed, parent, false)
        return ViewAdapter(view)
    }

    override fun getItemCount(): Int = borrow.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ViewAdapter,
        position: Int
    ) {
        val borrowList = borrow[position]

        // set text
        holder.apply {
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
        if (borrowList.isExpanded){
            TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
            holder.hiddenView.visibility = View.VISIBLE
            holder.arrowButton.setImageResource(
                if (isDarkMode) R.drawable.ic_expand_less_light_24
                else R.drawable.ic_expand_less_dark_24
            )
        }else{
            TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
            holder.hiddenView.visibility = View.GONE
            holder.arrowButton.setImageResource(
                if (isDarkMode) R.drawable.ic_expand_more_light_24
                else R.drawable.ic_expand_more_dark_24
            )
        }

        // action click
        val onItemClickListener = View.OnClickListener{
            val wasExpanded = borrowList.isExpanded

            collapseAllItemsExcept(position)
            borrowList.isExpanded = !wasExpanded
            notifyItemChanged(position)
        }

        holder.itemView.setOnClickListener(onItemClickListener)
        holder.arrowButton.setOnClickListener(onItemClickListener)

    }
    
    private fun collapseAllItemsExcept(position: Int) {
        for (i in borrow.indices) {
            if (i != position && borrow[i].isExpanded) {
                borrow[i].isExpanded = false
                notifyItemChanged(i)
            }
        }
    }


}