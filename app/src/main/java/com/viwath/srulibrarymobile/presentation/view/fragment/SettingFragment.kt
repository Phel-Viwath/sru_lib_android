/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentSettingBinding
import com.viwath.srulibrarymobile.presentation.view.activities.LoginActivity
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.utils.SettingPreferences

/**
 * SettingFragment is a Fragment that allows the user to customize their application settings.
 *
 * This includes options to:
 * - Change the application's theme (Light, Dark, System Default).
 * - View their username and user type/role.
 * - Log out of the application.
 *
 * It utilizes a `SettingPreferences` object to store and retrieve the user's chosen theme.
 * The Fragment also interacts with a `SettingViewModel` to handle user data and logout functionality.
 */
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingPreferences: SettingPreferences

    private val viewModel: SettingViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingPreferences = SettingPreferences(requireContext())
        // set up theme
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        setColor(isDarkMode)
        // theme option
        val themeMode = resources.getStringArray(R.array.theme) // get theme mode text from string.xml
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, themeMode) // create adapter for spinner that using android.R.layout.simple_spinner_item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // set dropdown view
        binding.themeSpinner.adapter = adapter // set adapter to spinner

        val currentTheme = settingPreferences.getSavedTheme()
        binding.themeSpinner.setSelection(currentTheme)
        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    1 -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                settingPreferences.saveTheme(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // username and role
        viewModel.username.observe(viewLifecycleOwner){ username ->
            binding.tvUsername.text = username
        }
        viewModel.userType.observe(viewLifecycleOwner){ role ->
            binding.tvUserType.text = role
        }
        // logout button
        binding.btnLogout.setOnClickListener{
            viewModel.logout()
            Intent(context, LoginActivity::class.java).apply {
                startActivity(this)
                requireActivity().finish()
            }
        }

    }
    // end of onViewCreated

    // change theme mode depend on user select
    private fun setTheme(themeMode: Int){
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    // set up theme when it start
    private fun setColor(isDarkMode: Boolean){
        val lightBackground = R.drawable.light_box_bg
        val nightBackground = R.drawable.night_box_bg
        binding.relativeUserSection.setBackgroundResource(if (isDarkMode) nightBackground else lightBackground)
        binding.linearSpinner.setBackgroundResource(if (isDarkMode) nightBackground else lightBackground)
    }

}// class