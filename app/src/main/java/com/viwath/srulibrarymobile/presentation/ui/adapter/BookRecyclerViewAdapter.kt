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
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.book.Book
import com.viwath.srulibrarymobile.utils.view_component.applyBlur
import com.viwath.srulibrarymobile.utils.view_component.getTranslucentColor
import eightbitlab.com.blurview.BlurView

/**
 * [BookRecyclerViewAdapter] is a RecyclerView adapter responsible for displaying a list of [Book] objects.
 *
 * It handles the creation of view holders, binding data to the views, and managing user interactions like
 * clicking on a book item or its associated menu.
 *
 * @property context The application context.
 * @property books The list of [Book] objects to display.
 * @property isDarkMode Boolean indicating whether dark mode is enabled. This affects the appearance of the menu icon.
 * @property onMenuItemClicked Callback function invoked when a menu item (update, delete, borrow) is clicked for a book.
 *          It takes the [Book] object and the action string ("update", "delete", "borrow") as parameters.
 * @property onItemClicked Callback function invoked when a book item is clicked. It takes the clicked [Book] object as a parameter.
 */
class BookRecyclerViewAdapter(
    private val context: Activity,
    private var books: List<Book>,
    private val isDarkMode: Boolean,
    private var isClassicMode: Boolean,
    private val onMenuItemClicked: (book: Book, action: String) -> Unit,
    private val onItemClicked: (book: Book) -> Unit
): RecyclerView.Adapter<BookRecyclerViewAdapter.BookViewHolder>(){

    class BookViewHolder(view: View): RecyclerView.ViewHolder(view){
        val rootView: MaterialCardView = view.findViewById(R.id.recyclerBookContainer)
        val tvTitle: TextView = view.findViewById(R.id.tvBookTitle)
        val tvBookQuan: TextView = view.findViewById(R.id.tvBookQuan)
        val tvLanguage: TextView = view.findViewById(R.id.tvLanguage)
        val menuIcon: ImageView = itemView.findViewById(R.id.menuIcon)
        val blurView: BlurView = view.findViewById(R.id.blurViewBookCard)
        var blurSetup = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        val menuIcon = holder.menuIcon
        if (isDarkMode)
            menuIcon.setImageResource(R.drawable.ic_more_vert_light_24)
        else
            menuIcon.setImageResource(R.drawable.ic_more_vert_night_24)

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
                    R.id.action_remove -> onMenuItemClicked(book, "remove")
                    R.id.action_borrow -> onMenuItemClicked(book, "borrow")
                }
                true
            }
            popUpMenu.show()
        }
        holder.itemView.setOnClickListener{ onItemClicked(book) }

//        if (!isClassicMode){
//            holder.rootView.apply {
//                setCardBackgroundColor(context.getTransparent())
//                radius = 8f
//                strokeColor = context.getTransparent()
//            }
//            holder.blurView.applyBlur(context, 10f, context.getTranslucentColor(isDarkMode))
//        }

        if (!isClassicMode) {
            if (!holder.blurSetup) {
                holder.blurView.applyBlur(context, 10f, context.getTranslucentColor(isDarkMode))
                holder.blurSetup = true
            }
            holder.blurView.setBlurEnabled(true)
        } else {
            holder.blurView.setBlurEnabled(false)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateViewMode(newMode: Boolean) {
        isClassicMode = newMode
        notifyDataSetChanged() // Refresh RecyclerView
    }

    private fun TextView.setTruncateText(text: String, maxLength: Int = 15) {
        this.text = if (text.length > 15) text.take(maxLength) + "..." else text
    }

}