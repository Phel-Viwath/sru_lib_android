/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.activities

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.ActivityMainBinding
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import dagger.hilt.android.AndroidEntryPoint


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = Loading(this)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM){
            binding.statusBar.visibility = View.VISIBLE
        }

        observeNetworkStatus()


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

    }


    // save fragment
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ACTIVE_FRAGMENT_TAG, activeFragmentTag)
    }

    fun observeNetworkStatus(){
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

    fun startLoading(): Unit = runOnUiThread{ loading.loadingStart() }

    fun stopLoading(): Unit = runOnUiThread{ loading.loadingDismiss()}

    fun showToast(message: String): Unit = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    fun hideKeyboard() {
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
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
        snackbar.show()
    }

    fun showSnackbar(message: String){
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    companion object{
        private const val ACTIVE_FRAGMENT_TAG = "active_fragment"
    }

}
