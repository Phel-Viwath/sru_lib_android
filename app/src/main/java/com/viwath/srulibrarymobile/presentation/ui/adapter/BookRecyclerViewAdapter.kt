/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.data.dto.BookDto

class BookRecyclerViewAdapter(
    private var books: List<BookDto>,
    private val isDarkMode: Boolean,
    private val onItemClicked: (book: BookDto) -> Unit
): RecyclerView.Adapter<BookRecyclerViewAdapter.BookViewHolder>(){

    inner class BookViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvTitle: TextView = view.findViewById(R.id.tvBookTitle)
        val tvBookQuan: TextView = view.findViewById(R.id.tvBookQuan)
        val tvLanguage: TextView = view.findViewById(R.id.tvLanguage)
        val rootView: ConstraintLayout = view.findViewById(R.id.recyclerBookContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        val root = holder.rootView
        if (isDarkMode)
            root.setBackgroundResource(R.drawable.night_recycler_book_background)
        else
            root.setBackgroundResource(R.drawable.light_recycler_book_background)

        //holder.tvTitle.text = book.bookTitle
        holder.tvTitle.setTruncateText(book.bookTitle)
        holder.tvBookQuan.text = "${book.bookQuan}"
        holder.tvLanguage.text = if (book.languageId === "kh") "Language.Khmer" else "Language.English"
        holder.itemView.setOnClickListener {
            onItemClicked(book)
        }
    }

    private fun TextView.setTruncateText(text: String, maxLength: Int = 15) {
        this.text = if (text.length > 15) text.substring(0, maxLength) + "..." else text
    }

}