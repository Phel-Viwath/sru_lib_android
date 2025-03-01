/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.adapter

import android.annotation.SuppressLint
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.ItemDonationBinding
import com.viwath.srulibrarymobile.domain.model.Donation

class DonationRecycleViewAdapter(
    private val donationList: List<Donation>,
    private val isDarkMode: Boolean,
    private val onEditClick: (Donation) -> Unit
): RecyclerView.Adapter<DonationRecycleViewAdapter.ViewHolder>(){

    inner class ViewHolder(
        private val binding: ItemDonationBinding
    ) : RecyclerView.ViewHolder(binding.root){

        val btEdit = binding.btEdit
        val cardView = binding.rootCardView
        val hiddenView = binding.hiddenView
        val fixedView = binding.fixedLayout

        @SuppressLint("SetTextI18n")
        fun bind(donation: Donation, no: Int){
            binding.tvNo.text = no.toString()
            binding.tvDonatorName.text = donation.donatorName
            binding.tvBookId.text = donation.bookId

            binding.tvBookTitle.text = "Book Title: "
            binding.tvCollege.text = "College: "
            binding.tvAuthor.text = "Author: "
            binding.tvGenre.text = "Genre: "
            binding.tvPublicYear.text = "Public Year: "
            binding.tvDonateDate.text = "Donate Date: "
            binding.tvDonateQuan.text = "Donate Quan: "

            binding.tvBookTitleValue.text = donation.bookTitle
            binding.tvCollegeValue.text = donation.collegeName
            binding.tvAuthorValue.text = donation.author
            binding.tvGenreValue.text = donation.genre
            binding.tvPublicYearValue.text = donation.publicationYear.toString()
            binding.tvDonateDateValue.text = donation.donateDate
            binding.tvDonateQuanValue.text = donation.bookQuan.toString()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDonationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = donationList.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val donation = donationList[position]
        holder.bind(donation, position + 1)

        if (donation.isExpanded){
            TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
            holder.hiddenView.visibility = View.VISIBLE
        }
        else{
            TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
            holder.hiddenView.visibility = View.GONE
        }

        holder.fixedView.setOnClickListener{
            val wasExpanded = donation.isExpanded
            collapseAllItemsExcept(position)
            donation.isExpanded = !wasExpanded
            notifyItemChanged(position)
        }

        holder.btEdit.apply {
            setImageResource(
                if (isDarkMode) R.drawable.ic_edit_light_24
                else R.drawable.ic_edit_night_24
            )
            setOnClickListener{
                onEditClick(donation)
            }
        }
    }

    private fun collapseAllItemsExcept(position: Int) {
        for (i in donationList.indices) {
            if (i != position && donationList[i].isExpanded) {
                donationList[i].isExpanded = false
                notifyItemChanged(i)
            }
        }
    }

}