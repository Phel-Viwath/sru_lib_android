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
import androidx.lifecycle.lifecycleScope
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentSettingBinding
import com.viwath.srulibrarymobile.presentation.view.activities.LoginActivity
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.CLASSIC
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.MODERN
import com.viwath.srulibrarymobile.utils.KeyStoreManager
import com.viwath.srulibrarymobile.utils.datastore.SettingPreferences
import kotlinx.coroutines.launch

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
        settingPreferences = SettingPreferences(requireContext(), KeyStoreManager())
        // set up theme
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        setColor(isDarkMode)
        // theme option

        setupView()
        observeViewmodel()

    }
    // end of onViewCreated

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupView(){
        val themeMode = resources.getStringArray(R.array.theme) // get theme mode text from string.xml
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, themeMode) // create adapter for spinner that using android.R.layout.simple_spinner_item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // set dropdown view
        binding.themeSpinner.adapter = adapter // set adapter to spinner
        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lifecycleScope.launch {
                    viewModel.setTheme(position)
                    when(position){
                        0 -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                        1 -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                        2 -> setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // logout button
        binding.btnLogout.setOnClickListener{
            viewModel.logout()
            Intent(context, LoginActivity::class.java).apply {
                startActivity(this)
                requireActivity().finish()
            }
        }

        binding.radioClassic.setOnClickListener {
            binding.radioModern.isChecked = false
            binding.radioClassic.isChecked = true
            viewModel.saveViewMode(CLASSIC)
            //alertDialog("Require restart application to apply classic mode")
        }

        binding.radioModern.setOnClickListener {
            binding.radioModern.isChecked = true
            binding.radioClassic.isChecked = false
            viewModel.saveViewMode(MODERN)
            //alertDialog("Require restart application to apply modern mode")
        }

    }

    private fun observeViewmodel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.tvUsername.text = state.username
                binding.tvUserType.text = state.userType
                binding.themeSpinner.setSelection(state.themeMode)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewMode.observe(viewLifecycleOwner){ viewMode ->
                when(viewMode){
                    CLASSIC -> {
                        binding.radioClassic.isChecked = true
                        binding.radioModern.isChecked = false
                    }
                    MODERN -> {
                        binding.radioModern.isChecked = true
                        binding.radioClassic.isChecked = false
                    }
                }
            }
        }
    }

    // change theme mode depend on user select
    private fun setTheme(themeMode: Int){
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    // set up theme when it start
    private fun setColor(isDarkMode: Boolean){
        val lightBackground = R.drawable.light_box_bg
        val nightBackground = R.drawable.night_box_bg

        val imgWhiteClassic = R.drawable.img_white_theme_96
        val imgBlackClassic = R.drawable.img_black_theme_96

        binding.relativeUserSection.setBackgroundResource(if (isDarkMode) nightBackground else lightBackground)
        binding.linearSpinner.setBackgroundResource(if (isDarkMode) nightBackground else lightBackground)
        binding.linearRadio.setBackgroundResource(if (isDarkMode) nightBackground else lightBackground)
        binding.ivClassic.setBackgroundResource(if (isDarkMode) imgWhiteClassic else imgBlackClassic)

    }


}// class