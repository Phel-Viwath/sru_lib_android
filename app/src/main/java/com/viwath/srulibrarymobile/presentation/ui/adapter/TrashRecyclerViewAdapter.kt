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
import com.viwath.srulibrarymobile.databinding.ItemRestoreBookBinding
import com.viwath.srulibrarymobile.domain.model.book.BookInTrash
import com.viwath.srulibrarymobile.utils.view_component.applyBlur
import com.viwath.srulibrarymobile.utils.view_component.getTranslucentColor
import com.viwath.srulibrarymobile.utils.view_component.getTransparent

class TrashRecyclerViewAdapter(
    private var isClassicMode: Boolean,
    private var books: List<BookInTrash>,
    private val activity: Activity,
    private val isDarkMode: Boolean,
    private val onClick: (BookInTrash) -> Unit
): RecyclerView.Adapter<TrashRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(
        private val binding: ItemRestoreBookBinding
    ): RecyclerView.ViewHolder(binding.root){
        val baseCardView = binding.baseCardview
        val blurView = binding.blurViewRestore
        val arrowButton = binding.arrowButton
        val hiddenView = binding.hiddenView

        @SuppressLint("SetTextI18n")
        fun bindData(book: BookInTrash, position: Int){
            binding.tvNo.text = (position + 1).toString()
            binding.tvBookTitle.text = book.bookTitle
            binding.tvBookIdValue.text = book.bookId
            binding.tvCollegeValue.text = book.collegeName
            binding.tvAuthorValue.text = book.author
            binding.tvGenreValue.text = book.genre
            binding.tvPublicYearValue.text = book.publicationYear.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val binding = ItemRestoreBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }
    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        val item = books[position]
        holder.bindData(item, position)
        holder.itemView.setOnClickListener{
            onClick(item)
        }
        expendAnimate(item.isExpended, holder)
        holder.apply {
            // animation
            arrowButton.setOnClickListener {
                val wasExpended = item.isExpended
                item.isExpended = !wasExpended
                collapseAllItemExcept(position)
                notifyItemChanged(position)
            }
        }
        // theme
        if(!isClassicMode){
            holder.baseCardView.apply {
                setCardBackgroundColor(activity.getTransparent())
                strokeColor = activity.getTransparent()
                radius = 5f
            }
            holder.blurView.applyBlur(
                activity,
                10f,
                activity.getTranslucentColor(isDarkMode)
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateViewMode(newMode: Boolean){
        isClassicMode = newMode
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateBookData(newBooks: List<BookInTrash>){
        books = newBooks
        notifyDataSetChanged()
    }

    private fun expendAnimate(isExpended: Boolean, holder: ItemViewHolder){
        if (isExpended){
            TransitionManager.beginDelayedTransition(holder.baseCardView, AutoTransition())
            holder.hiddenView.visibility = View.VISIBLE
            holder.arrowButton.setImageResource(
                if (isDarkMode) R.drawable.ic_expand_less_light_24
                else R.drawable.ic_expand_less_dark_24
            )
        }
        else{
            TransitionManager.beginDelayedTransition(holder.baseCardView, AutoTransition())
            holder.hiddenView.visibility = View.GONE
            holder.arrowButton.setImageResource(
                if (isDarkMode) R.drawable.ic_expand_more_light_24
                else R.drawable.ic_expand_more_dark_24
            )
        }
    }

    private fun collapseAllItemExcept(position: Int){
        for (i in books.indices){
            if (i != position && books[i].isExpended){
                books[i].isExpended = false
                notifyItemChanged(i)
            }
        }
    }

}