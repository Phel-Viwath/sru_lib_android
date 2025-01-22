/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentBorrowedTabBinding

class BorrowedFragment: Fragment(R.layout.fragment_borrowed_tab) {
    private var _binding: FragmentBorrowedTabBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBorrowedTabBinding.bind(view)
    }
}