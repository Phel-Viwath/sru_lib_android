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
import com.viwath.srulibrarymobile.databinding.FragmentBackupTabBinding

class BackupBookFragment : Fragment(R.layout.fragment_backup_tab) {
    private var _binding: FragmentBackupTabBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBackupTabBinding.bind(view)
    }
}
