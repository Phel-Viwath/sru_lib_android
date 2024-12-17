package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentBorrowedBinding

class BorrowedFragment: Fragment(R.layout.fragment_borrowed) {
    private var _binding: FragmentBorrowedBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBorrowedBinding.bind(view)
    }
}