/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity of the application.
 *
 * This activity serves as the entry point for the application and manages the navigation
 * between different fragments using the Navigation component. It utilizes a bottom
 * navigation view to provide easy access to the main sections of the app.
 *
 * @property binding The view binding for the activity's layout.
 * @property navController The navigation controller for managing fragment transitions.
 * @property activeFragmentTag The tag of the currently active fragment, used for state restoration.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var activeFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    fun hideBottomNav() {
        binding.bottomNavView.visibility = View.GONE
    }

    fun showBottomNav() {
        binding.bottomNavView.visibility = View.VISIBLE
    }


    companion object{
        private const val ACTIVE_FRAGMENT_TAG = "active_fragment"
    }

}
