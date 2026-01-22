/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.ItemEntryBinding
import com.viwath.srulibrarymobile.domain.model.entry.AttendDetail

class AttendRecyclerViewAdapter(
    private val activity: Activity,
    private val data: List<AttendDetail>,
    private val isDarkMode: Boolean,
    private var isClassicMode: Boolean
): RecyclerView.Adapter<AttendRecyclerViewAdapter.EntryViewHolder>(){

    class EntryViewHolder(
        private val binding: ItemEntryBinding
    ): RecyclerView.ViewHolder(binding.root){

        val cardView = binding.cardViewEntry

        fun bindData(entry: AttendDetail){
            binding.tvStudentId.text = entry.studentId.toString()
            binding.tvStudentName.text = entry.studentName
            binding.tvStatus.text = entry.status
        }
        fun setColorStatus(isOut: Boolean){
            if (isOut)
                binding.tvStatus.setTextColor(Color.RED)
            else
                binding.tvStatus.setTextColor(Color.BLUE)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntryViewHolder {
        val binding = ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntryViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(
        holder: EntryViewHolder,
        position: Int
    ) {
        val attend = data[position]
        val isOut = attend.status == "OUT"

        if (!isClassicMode) {
            // Modern mode = semi-transparent card
            holder.cardView.apply {
                // 80% opacity white in light mode / dark gray in dark mode
                setCardBackgroundColor(
                    if (isDarkMode) activity.getColor(R.color.opaque_charcoal)
                    else activity.getColor(R.color.opaque_white)
                )
                strokeColor = if (isDarkMode)
                    activity.getColor(R.color.translucent_white)
                else activity.getColor(R.color.translucent_black)
            }
        } else {
            // Classic mode = normal solid background
            holder.cardView.apply {
                setCardBackgroundColor(
                    if (isDarkMode)
                        activity.getColor(R.color.material_dark_gray)
                    else activity.getColor(R.color.solid_white)
                )
                strokeWidth = 1
            }
        }


        holder.bindData(attend)
        holder.setColorStatus(isOut)
        holder.itemView.setOnClickListener {
            showModal(context = holder.itemView.context, attend)
        }
    }


    @SuppressLint("InflateParams", "SetTextI18n")
    private fun showModal(context: Context, item: AttendDetail){
        val dialog = MaterialAlertDialogBuilder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

        dialogView.findViewById<TextView>(R.id.lbId).text = "Student ID:"
        dialogView.findViewById<TextView>(R.id.lbName).text = "Student Name:"
        dialogView.findViewById<TextView>(R.id.lbGender).text = "Gender:"
        dialogView.findViewById<TextView>(R.id.lbMajor).text = "Major:"
        dialogView.findViewById<TextView>(R.id.lbGeneration).text = "Generation:"
        dialogView.findViewById<TextView>(R.id.lbEntryTime).text = "Entry Time:"
        dialogView.findViewById<TextView>(R.id.lbExitingTime).text = "Exiting Time:"
        dialogView.findViewById<TextView>(R.id.lbPurpose).text = "Purpose:"
        dialogView.findViewById<TextView>(R.id.lbStatus).text = "Status:"

        dialogView.findViewById<TextView>(R.id.tvShowId).text = "${item.studentId}"
        dialogView.findViewById<TextView>(R.id.tvName).text = item.studentName
        dialogView.findViewById<TextView>(R.id.tvGender).text = item.gender
        dialogView.findViewById<TextView>(R.id.tvMajor).text = item.major
        dialogView.findViewById<TextView>(R.id.tvGeneration).text = "${item.generation}"
        dialogView.findViewById<TextView>(R.id.tvEntryTime).text = item.entryTimes
        dialogView.findViewById<TextView>(R.id.tvExitingTime).text = item.exitingTimes ?: "N/A"
        dialogView.findViewById<TextView>(R.id.tvPurpose).setTruncateText(item.purpose)
        dialogView.findViewById<TextView>(R.id.tvStatus).text = item.status
        if(item.status == "OUT"){
            dialogView.findViewById<TextView>(R.id.tvStatus).setTextColor(Color.RED)
        }else{
            dialogView.findViewById<TextView>(R.id.tvStatus).setTextColor(Color.BLUE)
        }

        dialog.setView(dialogView)
        dialog.background = Color.TRANSPARENT.toDrawable()
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun TextView.setTruncateText(text: String, maxLength: Int = 15) {
        this.text = if (text.length > 15) text.take(maxLength) + "..." else text
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateViewMode(isClassic: Boolean){
        if (this.isClassicMode == isClassic) return
        this.isClassicMode = isClassic
        notifyDataSetChanged()
    }

}