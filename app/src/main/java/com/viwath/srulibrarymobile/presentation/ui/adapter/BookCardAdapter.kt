/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viwath.srulibrarymobile.databinding.ItemBookCardBinding
import com.viwath.srulibrarymobile.domain.model.book.BookCard
import com.viwath.srulibrarymobile.utils.applyBlur
import com.viwath.srulibrarymobile.utils.getTranslucentColor
import com.viwath.srulibrarymobile.utils.getTransparent

class BookCardAdapter(
    private val context: Activity,
    private val data: List<BookCard>,
    private var isClassicMode: Boolean,
    private var isDarkMode: Boolean
): RecyclerView.Adapter<BookCardAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemBookCardBinding): RecyclerView.ViewHolder(binding.root) {
        @Suppress("SetTextI18n")
        fun bind(item: BookCard) {
            binding.tvCardTitle.text = item.title
            binding.ivBook.setImageResource(item.icon)
            binding.tvValue.text = item.value.toString()
        }
        val blurView = binding.blurViewBookInfo
        val cardView = binding.cardBook
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        val translucentColor = context.getTranslucentColor(isDarkMode)

        if (!isClassicMode){
            holder.cardView.setCardBackgroundColor(context.getTransparent())
            holder.blurView.applyBlur(context, 25f, translucentColor)
        }
    }

    override fun getItemCount(): Int = data.size
    fun getData(): List<BookCard> = data
}