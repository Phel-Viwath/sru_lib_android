/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.entry.AttendDetail

/**
 * EntryRecycleViewAdapter is a RecyclerView.Adapter that displays a list of AttendDetail objects.
 * It also displays a header row with column labels.
 */
class EntryRecycleViewAdapter(
    private val list: List<AttendDetail>
) : RecyclerView.Adapter<EntryRecycleViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvId: TextView = view.findViewById(R.id.tvStudentId)
        val tvGender: TextView = view.findViewById(R.id.tvGender)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attend_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size + 1

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            VIEW_TYPE_HEADER -> {
                holder.apply {
                    tvId.text = "ID"
                    tvStudentName.text = "Name"
                    tvGender.text = "Gender"
                    tvStatus.text = "Status"
                }
            }

            VIEW_TYPE_ITEM -> {
                val attendDetail = list[position - 1]
                holder.apply {
                    tvId.text = "${attendDetail.studentId}"
                    tvStudentName.text = attendDetail.studentName
                    tvGender.text = attendDetail.gender
                    tvStatus.text = attendDetail.status
                }

                holder.itemView.setOnClickListener {
                    showModal(context = holder.itemView.context, attendDetail)
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
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
        dialog.setBackground(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun TextView.setTruncateText(text: String, maxLength: Int = 15) {
        this.text = if (text.length > 15) text.substring(0, maxLength) + "..." else text
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}