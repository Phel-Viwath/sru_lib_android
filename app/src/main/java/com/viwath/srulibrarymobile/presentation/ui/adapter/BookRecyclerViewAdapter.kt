/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.Book

class BookRecyclerViewAdapter(
    private val context: Context,
    private var books: List<Book>,
    private val isDarkMode: Boolean,
    private val onMenuItemClicked: (book: Book, action: String) -> Unit,
    private val onItemClicked: (book: Book) -> Unit
): RecyclerView.Adapter<BookRecyclerViewAdapter.BookViewHolder>(){

    inner class BookViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvTitle: TextView = view.findViewById(R.id.tvBookTitle)
        val tvBookQuan: TextView = view.findViewById(R.id.tvBookQuan)
        val tvLanguage: TextView = view.findViewById(R.id.tvLanguage)
        val rootView: ConstraintLayout = view.findViewById(R.id.recyclerBookContainer)
        val menuIcon: ImageView = itemView.findViewById(R.id.menuIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        val root = holder.rootView
        val menuIcon = holder.menuIcon
        if (isDarkMode){
            root.setBackgroundResource(R.drawable.night_recycler_book_background)
            menuIcon.setImageResource(R.drawable.ic_more_vert_light_24)
        }
        else{
            root.setBackgroundResource(R.drawable.light_recycler_book_background)
            menuIcon.setImageResource(R.drawable.ic_more_vert_night_24)
        }

        //holder.tvTitle.text = book.bookTitle
        holder.tvTitle.setTruncateText(book.bookTitle)
        holder.tvBookQuan.text = "${book.bookQuan}"
        holder.tvLanguage.text = if (book.languageId === "kh") "Khmer. ${book.bookId}" else "English. ${book.bookId}"
        menuIcon.setOnClickListener {
            val popUpMenu = PopupMenu(context, holder.menuIcon)
            val inflater: MenuInflater = popUpMenu.menuInflater
            inflater.inflate(R.menu.menu_book_option, popUpMenu.menu)
            popUpMenu.setOnMenuItemClickListener{ menuItem: MenuItem ->
                when(menuItem.itemId){
                    R.id.action_update -> onMenuItemClicked(book, "update")
                    R.id.action_delete -> onMenuItemClicked(book, "delete")
                    R.id.action_borrow -> onMenuItemClicked(book, "borrow")
                }
                true
            }
            popUpMenu.show()
        }
        holder.itemView.setOnClickListener{ onItemClicked(book) }
    }

    private fun TextView.setTruncateText(text: String, maxLength: Int = 15) {
        this.text = if (text.length > 15) text.substring(0, maxLength) + "..." else text
    }

}