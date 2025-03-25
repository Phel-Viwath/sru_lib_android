/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viwath.srulibrarymobile.databinding.ItemBookCardBinding
import com.viwath.srulibrarymobile.domain.model.book.BookCard

class BookCardAdapter(
    private val data: List<BookCard>,
): RecyclerView.Adapter<BookCardAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemBookCardBinding): RecyclerView.ViewHolder(binding.root) {
        @Suppress("SetTextI18n")
        fun bind(item: BookCard) {
            binding.tvCardTitle.text = item.title
            binding.ivBook.setImageResource(item.icon)
            binding.tvValue.text = item.value.toString()
        }
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
    }

    override fun getItemCount(): Int = data.size

}