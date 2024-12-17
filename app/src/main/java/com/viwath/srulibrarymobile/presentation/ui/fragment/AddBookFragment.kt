package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentAddBookBinding
import com.viwath.srulibrarymobile.presentation.ui.activities.MainActivity

class AddBookFragment : Fragment(R.layout.fragment_add_book) {

    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddBookBinding.bind(view)

        binding.edtSearch.setOnFocusChangeListener{_, hasFocus ->
            val activity = requireActivity()
            if (hasFocus){
                if (activity is MainActivity)
                    activity.hideBottomNav()
            }else {
                if (activity is MainActivity)
                    activity.showBottomNav()
            }
        }

        binding.rootLayout.setOnTouchListener { _, _ ->
            binding.edtSearch.clearFocus()
            val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            false
        }

    }

}