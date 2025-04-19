/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.databinding.ActivityRegisterBinding
import com.viwath.srulibrarymobile.presentation.event.AuthEvent
import com.viwath.srulibrarymobile.presentation.viewmodel.AuthViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * [RegisterActivity] is the activity responsible for handling user registration.
 *
 * This activity allows users to create a new account by providing a username,
 * password, and email. It also provides a link to the [LoginActivity] for users
 * who already have an account.
 *
 * The activity interacts with [AuthViewModel] to perform the registration logic
 * and [ConnectivityViewModel] to check for network connectivity.
 *
 * It also uses [Loading] to display a loading indicator while waiting for network responses.
 *
 * Uses [ActivityRegisterBinding] for view binding.
 *
 * Uses [MaterialAlertDialogBuilder] to show message dialogs.
 */
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity(){

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var loading: Loading
    private val viewModel: AuthViewModel by viewModels()
    private val connectivityViewModel: ConnectivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = Loading(this)

        //val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // Check connection
        connectivityViewModel.networkStatus.observe(this){ isConnected ->
            if (isConnected)
                observeViewModel()
            else
                dialogMessage("No Internet Connection", "System")
        }

        setUpView()

    }

    private fun setUpView(){
        val state = viewModel.state

        val fullText = getString(R.string.register_login_hint)
        val boldText = "Sign In"
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf(boldText)
        val endIndex = startIndex + boldText.length
        spannable.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            startIndex,
            endIndex,
            android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvGoSignIn.text = spannable
        binding.tvGoSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.edtUsername.setText(state.value.signUpUsername)
        binding.edtPassword.setText(state.value.signUpPassword)
        binding.edtEmail.setText(state.value.signUpEmail)

        binding.btRegister.setOnClickListener {
            val username = binding.edtUsername.text
            val password = binding.edtPassword.text
            val email = binding.edtEmail.text
            if (username.isNullOrBlank() || password.isNullOrBlank() || email.isNullOrBlank()){
                dialogMessage(message = "Please enter username, password and email.", title = "Error!")
                return@setOnClickListener
            }
            viewModel.onEvent(AuthEvent.SignUpUsernameChanged(username.toString()))
            viewModel.onEvent(AuthEvent.SignUpPasswordChanged(password.toString()))
            viewModel.onEvent(AuthEvent.SignUpEmailChanged(email.toString()))
            viewModel.onEvent(AuthEvent.SignUp)
        }
    }

    private fun observeViewModel(){
        lifecycleScope.launch {
            viewModel.authResult.collect {result ->
                when(result){
                    is AuthResult.Unauthorized -> {
                        dialogMessage(
                            "Please check your email.",
                            "Unauthorized"
                        )
                    }
                    is AuthResult.Authorize -> {
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        this@RegisterActivity.finish()
                    }
                    is AuthResult.UnknownError -> {
                        dialogMessage(
                            "Unknown error happen",
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
                dialogMessage(message = message, title = "Error!")
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


    private fun dialogMessage(message: String, title: String){
        runOnUiThread {
            if (!isFinishing && !isDestroyed)
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
}