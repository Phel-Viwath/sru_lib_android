/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.viwath.srulibrarymobile.databinding.FragmentStudentBinding

/**
 * StudentFragment is a Fragment responsible for displaying and managing the student-related
 * content within the application.
 *
 * This fragment utilizes view binding to interact with its layout, defined in `fragment_student.xml`.
 * It inflates the layout during the `onCreateView` lifecycle method and performs
 * any necessary UI setup in `onViewCreated`.
 */
class StudentFragment : Fragment() {

    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}