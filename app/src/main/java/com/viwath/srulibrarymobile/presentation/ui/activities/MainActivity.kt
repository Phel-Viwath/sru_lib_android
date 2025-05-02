/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */


@file:Suppress("DEPRECATION")

package com.viwath.srulibrarymobile.presentation.ui.activities

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.ActivityMainBinding
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.CLASSIC
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.MODERN
import com.viwath.srulibrarymobile.utils.view_component.applyBlur
import com.viwath.srulibrarymobile.utils.view_component.getTranslucentColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


/**
 * The main entry point of the application.
 *
 * This activity manages the main navigation flow using the Navigation Component,
 * handles network connectivity changes, displays loading indicators,
 * and provides utility functions for common UI operations.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var loading: Loading

    private var activeFragmentTag: String? = null
    private val connectivityViewModel: ConnectivityViewModel by viewModels()
    private val settingViewModel: SettingViewModel by viewModels()

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.background = null
        loading = Loading(this)
        observeNetworkStatus()

        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        lifecycleScope.launch {
            settingViewModel.viewMode.observe(this@MainActivity) {
                val isClassicMode = when(it){
                    CLASSIC -> true
                    MODERN -> false
                    else -> true
                }
                setUpView(isDarkMode, isClassicMode)
            }
        }

        binding.bottomNavViewBlurView.applyBlur(this, 10f, this.getTranslucentColor(isDarkMode))

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavView.setupWithNavController(navController)

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> {
                    navController.navigate(R.id.nav_dashboard)
                    true
                }
                R.id.nav_entry -> {
                    navController.navigate(R.id.nav_entry)
                    true
                }
                R.id.nav_book -> {
                    navController.navigate(R.id.nav_book)
                    true
                }
                R.id.nav_student -> {
                    navController.navigate(R.id.nav_student)
                    true
                }
                R.id.nav_setting -> {
                    navController.navigate(R.id.nav_setting)
                    true
                }
                else -> false
            }
        }

        binding.bottomNavView.itemTextColor = ContextCompat.getColorStateList(this, R.color.bottom_nav_label_color)
        binding.bottomNavView
            .itemIconTintList = ContextCompat.getColorStateList(
                this,
                if (isDarkMode) R.color.bottom_nav_dark_icon_color
                else R.color.bottom_nav_light_icon_color
            )

    }


    // save fragment
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ACTIVE_FRAGMENT_TAG, activeFragmentTag)
    }

    private fun observeNetworkStatus(){
        connectivityViewModel.networkMessage.observe(this){ message ->
            message?.let {
                if (it == "Connected"){
                    showTopSnackbar(it, false)
                }
                if (it == "No Internet Connection"){
                    showTopSnackbar(it, true)
                }
                connectivityViewModel.onSnackBarShown()
            }
        }
    }

    fun hideBottomNav() { binding.bottomNavView.visibility = View.GONE }

    fun showBottomNav() { binding.bottomNavView.visibility = View.VISIBLE }

    fun startLoading(): Unit = runOnUiThread{ loading.startLoading() }

    fun stopLoading(): Unit = runOnUiThread{ loading.stopLoading()}

    fun showTopSnackbar(message: String){
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val view = snackbar.view

        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER
        textView.textSize = 16f
        textView.setTextColor(Color.WHITE)
        textView.setTypeface(textView.typeface, Typeface.BOLD)

        view.setBackgroundColor(getColor(R.color.dark_shade_purple))
        view.backgroundTintList = null
        view.backgroundTintMode = null

        val param = view.layoutParams as CoordinatorLayout.LayoutParams
        param.gravity = Gravity.TOP
        view.layoutParams = param
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            param.topMargin = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.layoutParams = param
            WindowInsetsCompat.CONSUMED
        }

        snackbar.show()
    }

    fun showTopSnackbar(message: String, isDisconnected: Boolean){
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val view = snackbar.view

        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER
        textView.textSize = 16f
        textView.setTextColor(Color.WHITE)
        textView.setTypeface(textView.typeface, Typeface.BOLD)

        if (isDisconnected)
            view.setBackgroundColor(Color.RED)
        else
            view.setBackgroundColor(getColor(R.color.green))

        view.backgroundTintList = null
        view.backgroundTintMode = null

        val param = view.layoutParams as CoordinatorLayout.LayoutParams
        param.gravity = Gravity.TOP
        view.layoutParams = param
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            param.topMargin = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.layoutParams = param
            WindowInsetsCompat.CONSUMED
        }
        snackbar.show()
    }
    fun showDialog(title: String, message: String){
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton("Ok"){ dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun showSnackbar(message: String){
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    fun hideKeyboard(){
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    fun showToast(message: String): Unit = Toast.makeText(this, message, Toast.LENGTH_LONG).show()


    private fun setUpView(isDarkMode: Boolean, isClassicMode: Boolean){
        when(isClassicMode){
            true ->{
                binding.root.setBackgroundResource(if (isDarkMode) R.color.dark_background else R.color.light_background)
            }
            false -> {
                binding.root.setBackgroundResource(if (isDarkMode) R.drawable.img_dark_bg else R.drawable.img_light_bg_3)
            }
        }
    }

    companion object{
        private const val ACTIVE_FRAGMENT_TAG = "active_fragment"
    }

}
