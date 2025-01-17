/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.modal

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.checkbox.MaterialCheckBox
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.Generation
import com.viwath.srulibrarymobile.domain.model.MajorName
import com.viwath.srulibrarymobile.domain.model.Purpose
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.StudentName

class ModalEntry(view: View) {
    val purposes = mutableListOf<String>()
    val usePC = "Use PC"
    val reading = "Reading"
    val assignment = "Assignment"
    val other = "Other"

    val btnFind = view.findViewById<ImageButton>(R.id.btFind)
    val btEntry = view.findViewById<Button>(R.id.btn_submit)

    val edtStudentId = view.findViewById<EditText>(R.id.et_student_id)
    val tvStudentName = view.findViewById<TextView>(R.id.tv_student_name)
    val tvMajorName = view.findViewById<TextView>(R.id.tv_major_name)
    val tvGeneration = view.findViewById<TextView>(R.id.tv_generation)
    // radio button
    val cbReading = view.findViewById<MaterialCheckBox>(R.id.rb_read_book)
    val cbAssignment = view.findViewById<MaterialCheckBox>(R.id.rb_assignment)
    val cbUsePc= view.findViewById<MaterialCheckBox>(R.id.rb_use_pc)
    val cbOther = view.findViewById<MaterialCheckBox>(R.id.rb_other)

    fun entryPurpose(): Pair<StudentId, Purpose?>{
        val studentId = edtStudentId.text.toString()
        if (cbReading.isChecked) purposes.add(reading)
        if (cbAssignment.isChecked) purposes.add(assignment)
        if (cbUsePc.isChecked) purposes.add(usePC)
        if (cbOther.isChecked) purposes.add(other)
        val purpose = purposes.joinToString(", ")
        return Pair(studentId.toLong(), purpose)
    }

    @SuppressLint("SetTextI18n")
    fun setSearchResult(studentName: StudentName, majorName: MajorName, generation: Generation){
        tvStudentName.text = "Name: $studentName"
        tvGeneration.text = "Generation: $generation"
        tvMajorName.text = "Major: $majorName"
    }

    fun getStudentId(): StudentId?{
        val studentId = edtStudentId.text.toString().toLongOrNull()
        return studentId
    }

}