/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.databinding.ActivityLoginBinding
import com.viwath.srulibrarymobile.presentation.event.AuthEvent
import com.viwath.srulibrarymobile.presentation.viewmodel.AuthViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.utils.IntentString.EMAIL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * LoginActivity handles user authentication in the SRU Library Mobile application
 *
 * Key Responsibilities:
 * - Manage user login process
 * - Handle network connectivity
 * - Display error messages and loading states
 * - Navigate to registration or main application flow
 *
 * @AndroidEntryPoint Enables Dagger Hilt dependency injection
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity(){

    /**
     * View Binding and ViewModel Dependencies
     * - binding: Provides access to UI elements
     * - loading: Custom loading indicator
     * - viewModel: Manages authentication state and events
     * - connectivityViewModel: Monitors network connectivity
     */
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loading: Loading
    private val viewModel: AuthViewModel by viewModels()
    private val connectivityViewModel: ConnectivityViewModel by viewModels()

    /**
     * Activity Lifecycle Method
     * Initializes UI, sets up event listeners, and observes network and authentication states
     *
     * @param savedInstanceState Previous activity state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loading = Loading(this)

        uiEvent()

        connectivityViewModel.networkStatus.observe(this){ isConnected ->
            if (isConnected){
                observerViewModel()
            }else{
                showSnackbar("No Internet Connection")
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (!isNetworkConnected()){
            dialogMessage("No Internet Connection", "System")
        }
    }

    /**
     * Handles UI initialization and event binding
     * - Pre-fills email from intent extras
     * - Sets up dark/light mode line styling
     * - Configures login, create account, and OTP request buttons
     */
    private fun uiEvent(){
        val retrievedEmail = intent.getStringExtra(EMAIL)
        if (!retrievedEmail.isNullOrEmpty())
            binding.edtEmail.post{ binding.edtEmail.setText(retrievedEmail) }

        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.line.setBackgroundColor(if (isDarkMode) Color.WHITE else Color.BLACK)

        binding.btCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvGoGetOtp.setOnClickListener {
            startActivity(Intent(this, RequestOtpActivity::class.java))
        }
        val state = viewModel.state

        binding.edtEmail.setText(state.value.signInEmail)
        binding.edtPassword.setText(state.value.signInPassword)

        binding.btLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()){
                dialogMessage("Please enter username and password.", "Error!")
                return@setOnClickListener
            }
            viewModel.onEvent(AuthEvent.SignInUsernameChanged(email))
            viewModel.onEvent(AuthEvent.SignInPasswordChanged(password))
            viewModel.onEvent(AuthEvent.SignIn)
        }
    }

    /**
     * Observes ViewModel states and handles different authentication scenarios
     * - Authentication result processing
     * - Exception handling
     * - Loading state management
     */
    private fun observerViewModel(){
        lifecycleScope.launch {
            viewModel.authResult.collect {result ->
                when(result){
                    is AuthResult.Unauthorized -> {
                        dialogMessage(
                            "Please check your email or password.",
                            "Unauthorized"
                        )
                    }
                    is AuthResult.Authorize -> {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        this@LoginActivity.finish()
                    }
                    is AuthResult.UnknownError -> {
                        dialogMessage(
                            "Server problem...",
                            "Message"
                        )
                    }
                    is AuthResult.InternalServerError -> {
                        dialogMessage(
                            "Opp!, Internal server error!",
                            "Message"
                        )
                    }
                    is AuthResult.BadRequest -> {
                        dialogMessage(
                            "Bad Request!",
                            "Message"
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.exceptionFlow.collect{ message ->
                dialogMessage(message, "Error!")
            }
        }

        lifecycleScope.launch{
            viewModel.isLoading.collect{
                runOnUiThread {
                    if (it) loading.loadingStart()
                    else loading.loadingDismiss()
                }
            }
        }
    }

    /**
     * Utility Methods
     * - Display dialog messages
     * - Show snackbar notifications
     * - Check network connectivity
     */
    private fun dialogMessage(message: String, title: String){
        runOnUiThread {
            if(!isFinishing && !isDestroyed)
                MaterialAlertDialogBuilder(this)
                    .setMessage(message)
                    .setTitle(title)
                    .setCancelable(true)
                    .setPositiveButton("OK"){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }
    }

    private fun showSnackbar(message: String){
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val view = snackbar.view
        val tv = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackbar.show()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

}