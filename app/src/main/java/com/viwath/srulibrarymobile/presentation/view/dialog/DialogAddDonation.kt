/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.viwath.srulibrarymobile.databinding.DialogDonationBinding
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.CollegeId
import com.viwath.srulibrarymobile.domain.model.Donation
import com.viwath.srulibrarymobile.domain.model.DonationIO
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.model.LanguageId

class DialogAddDonation(view: View) {

    private val binding = DialogDonationBinding.bind(view)

    private val _languages: MutableList<Language> = mutableListOf()
    private val _college: MutableList<College> = mutableListOf()
    private var collegeId: String = ""
    private var languageId: String = ""

    fun setupSpinners(context: Context, languages: List<Language>, colleges: List<College>) {
        _languages.clear()
        _college.clear()
        _languages.addAll(languages)
        _college.addAll(colleges)
        val spinnerLayout = android.R.layout.simple_spinner_dropdown_item

        val languageAdapter = ArrayAdapter<String>(context, spinnerLayout, languages.map { it.languageName })
        val collegeAdapter = ArrayAdapter<String>(context, spinnerLayout, colleges.map { it.collegeName })

        binding.spinnerLanguage.adapter = languageAdapter
        binding.spinnerCollege.adapter = collegeAdapter

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                languageId = languages[position].languageId // Save selected language ID
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerCollege.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                collegeId = colleges[position].collegeId // Save selected college ID
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun onButtonChange(onButtonChange: (button: View) -> Unit){
        binding.btUploadFile.apply {
            onButtonChange(this)
        }
    }
    fun onButtonClick(
        onSubmitClick: (DonationIO) -> Unit,
        onUploadFileClick: () -> Unit
    ){
        binding.btSubmit.setOnClickListener{
            val donatorName = binding.edtDonatorName.text.toString()
            val bookId = binding.edtBookId.text.toString()
            val bookTitle = binding.edtBookTitle.text.toString()
            val author = binding.edtAuthor.text.toString()
            val bookGenre = binding.edtBookGenre.text.toString()
            val publicYear = binding.edtPublicYear.text.toString().trim().toIntOrNull()
            val bookQuan = binding.edtBookQuan.text.toString().trim().toInt()
            val donateDate = binding.edtDonateDate.text.toString().trim().toString()
            val donation = DonationIO(
                donatorId = null,
                donatorName = donatorName,
                bookId = bookId,
                bookTitle = bookTitle,
                bookQuan = bookQuan,
                author = author,
                genre = bookGenre,
                publicationYear = publicYear,
                collegeId = getSelectedIds().first,
                languageId = getSelectedIds().second,
                donateDate = donateDate
            )
            onSubmitClick(donation)
        }

        binding.btUploadFile.setOnClickListener{
            onUploadFileClick()
        }

    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNCHECKED_CAST")
    fun populateData(donation: Donation){
        binding.edtDonatorName.setText(donation.donatorName)
        binding.edtBookId.setText(donation.bookId)
        binding.edtBookTitle.setText(donation.bookTitle)
        binding.edtAuthor.setText(donation.author)
        binding.edtBookGenre.setText(donation.genre)
        binding.edtPublicYear.setText(donation.publicationYear.toString())
        binding.edtBookQuan.setText("${donation.bookQuan}")
        binding.edtDonateDate.setText(donation.donateDate)
        collegeId = _college.find { it.collegeName == donation.collegeName }?.collegeId ?: ""
        languageId = _languages.find { it.languageName == donation.languageName }?.languageId ?: ""

        Log.d("DialogAddDonation", "populateData: CollegeId: $collegeId languageId: $languageId")

        val languagesPosition = (binding.spinnerLanguage.adapter as ArrayAdapter<String>).getPosition(
            _languages.find { it.languageName == donation.languageName }?.languageName ?: ""
        )
        Log.d("DialogAddDonation", "populateData: $languagesPosition")
        val collegesPosition = (binding.spinnerCollege.adapter as ArrayAdapter<String>).getPosition(
            _college.find { it.collegeName == donation.collegeName }?.collegeName ?: ""
        )
        Log.d("DialogAddDonation", "populateData: $collegesPosition")

        binding.spinnerLanguage.setSelection(languagesPosition)
        binding.spinnerCollege.setSelection(collegesPosition)

    }

    private fun getSelectedIds(): Pair<CollegeId, LanguageId> {
        return Pair(collegeId, languageId)
    }



}