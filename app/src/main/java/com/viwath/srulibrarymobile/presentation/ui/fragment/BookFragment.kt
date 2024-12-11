package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentBookBinding
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class BookFragment : Fragment(R.layout.fragment_book){

    private var _binding: FragmentBookBinding?= null
    private val binding get() = _binding!!
    private var previousClickButton: MaterialButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookBinding.bind(view)

        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val cardList = listOf(binding.cardTotalBook, binding.cardDonateBook, binding.cardBorrow, binding.cardExp, binding.cardBorrowToday, binding.cardReturn)
        val buttonList = listOf(binding.btAddBook, binding.btBorrow, binding.btDonation, binding.btBackup)


        setUpColor(buttonList, isDarkMode)
        updateButtonBackgroundOnClick(isDarkMode = isDarkMode)

    }

    private fun setUpColor(listOfButton: List<MaterialButton>, isDarkMode: Boolean){
        listOfButton.forEach { materialButton ->
            materialButton.setStrokeColorResource(
                if (isDarkMode) R.color.white
                else R.color.black
            )
        }
    }
    private fun updateButtonBackgroundOnClick(isDarkMode: Boolean){
        val textWhite = resources.getColor(R.color.text_white)
        val textBlack = resources.getColor(R.color.text_black)
        val darkBlue = resources.getColor(R.color.dark_blue)
        val buttonClicked = {button: MaterialButton ->
            with(previousClickButton){
                this?.setTextColor(if (isDarkMode) textWhite else textBlack)
                this?.setStrokeColorResource(if (isDarkMode) R.color.white else R.color.black)
            }
            button.setTextColor(darkBlue)
            button.setStrokeColorResource(R.color.dark_blue)
            previousClickButton = button
        }
        binding.btAddBook.setOnClickListener{
            buttonClicked(binding.btAddBook)

        }
        binding.btBorrow.setOnClickListener{
            buttonClicked(binding.btBorrow)
        }
        binding.btDonation.setOnClickListener{
            buttonClicked(binding.btDonation)
        }
        binding.btBackup.setOnClickListener{
            buttonClicked(binding.btBackup)
        }
    }

}