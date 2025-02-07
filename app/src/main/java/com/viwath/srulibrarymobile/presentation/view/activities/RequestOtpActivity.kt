/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.ActivityRequestOtpBinding
import com.viwath.srulibrarymobile.presentation.event.OtpEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.viewmodel.OtpViewModel
import com.viwath.srulibrarymobile.utils.IntentString.EMAIL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * RequestOtpActivity
 *
 * This activity is responsible for handling the request OTP (One-Time Password) flow.
 * It allows the user to input their email address and request an OTP to be sent to that email.
 * Upon successful request, the activity navigates the user to the VerifyOtpActivity.
 *
 * Features:
 * - User interface for entering the email address.
 * - Button to request an OTP.
 * - Navigation to the LoginActivity.
 * - Display loading indicators during network operations.
 * - Display success/error messages using Snackbar and Toast.
 * - Observe and handle different states and events from the OtpViewModel.
 *
 * Dependencies:
 * - Hilt for dependency injection.
 * - Data Binding for view binding.
 * - Coroutines for asynchronous operations.
 * - ViewModel for managing UI-related data.
 * - Loading class for displaying loading indicator.
 * - OtpViewModel, OtpEvent, ResultEvent to perform and handle OTP events.
 */
@AndroidEntryPoint
class RequestOtpActivity : AppCompatActivity(){

    private lateinit var binding: ActivityRequestOtpBinding
    private lateinit var loading: Loading
    private var email = ""

    private val viewModel: OtpViewModel by lazy {
        ViewModelProvider(this)[OtpViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = Loading(this)

        binding.tvGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }

        binding.btRequestOtp.setOnClickListener {
            email = binding.edtEmail.text.toString().trim()
            if (email.isNotEmpty())
                viewModel.onEvent(OtpEvent.RequestOtp(email))
            else showToast("Please enter a valid email.")
        }

        // event
        observeEvent()
        //state
        observeOtpEvent()

    }

    private fun observeOtpEvent(){
        lifecycleScope.launch{
            viewModel.otpEvent.collect{ event ->
                when(event){
                    is OtpEvent.StartLoading -> showLoading()
                    is OtpEvent.StopLoading -> stopLoading()
                    is OtpEvent.VerifyNavigate -> {
                        stopLoading()
                        startActivity(
                            Intent(this@RequestOtpActivity, VerifyOtpActivity::class.java)
                                .putExtra(EMAIL, email)
                        )
                        this@RequestOtpActivity.finish()
                    }
                    else -> stopLoading()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeEvent() {
        lifecycleScope.launch {
            viewModel.event.collect { event ->
                when (event) {
                    is ResultEvent.ShowSuccess -> showSnackbar(event.message)
                    is ResultEvent.ShowError -> showToast(event.errorMsg)
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        runOnUiThread{
            val rootLayout = binding.root
            Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showToast(message: String) {
        runOnUiThread{
            Toast.makeText(this@RequestOtpActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showLoading(){
        runOnUiThread{
            loading.loadingStart()
        }
    }
    private fun stopLoading(){
        runOnUiThread{
            loading.loadingDismiss()
        }
    }

}