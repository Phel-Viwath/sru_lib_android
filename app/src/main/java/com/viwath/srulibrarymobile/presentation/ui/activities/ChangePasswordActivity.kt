/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.ActivityChangePasswordBinding
import com.viwath.srulibrarymobile.presentation.event.ChangePasswordEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.viewmodel.ChangePasswordViewModel
import com.viwath.srulibrarymobile.utils.IntentString.EMAIL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity for changing the user's password.
 *
 * This activity allows users to change their password after receiving a password reset link.
 * It handles user input, validation, and interaction with the {@link ChangePasswordViewModel}
 * to perform the password change operation.
 *
 * @see ChangePasswordViewModel
 * @see ChangePasswordEvent
 * @see ResultEvent
 * @see Loading
 */
@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity(){

    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()
    private lateinit var loading: Loading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = Loading(this)

        val email = intent.getStringExtra(EMAIL)

        binding.tvGoLogin.setOnClickListener {
            startActivity(Intent(this@ChangePasswordActivity, LoginActivity::class.java))
            this.finish()
        }
        binding.btConfirm.setOnClickListener {
            if (email != null) viewModel.onEvent(ChangePasswordEvent.EmailChange(email))
            else return@setOnClickListener

            val newPassword = binding.edtNewPassword.text.toString().trim()
            val confirmPassword = binding.edtConfirmPassword.text.toString().trim()

            viewModel.onEvent(ChangePasswordEvent.NewPasswordChange(newPassword))
            viewModel.onEvent(ChangePasswordEvent.ConfirmPasswordChange(confirmPassword))
            viewModel.onEvent(ChangePasswordEvent.OnChangePassword)
        }
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (binding.edtNewPassword.text.toString() != state.newPassword){
                    binding.edtNewPassword.setText(state.newPassword)
                }
                if (binding.edtConfirmPassword.text.toString() != state.confirmPassword){
                    binding.edtConfirmPassword.setText(state.confirmPassword)
                }
                if (state.isLoading) loadingStart() else loadingStop()
            }
        }

        lifecycleScope.launch {
            viewModel.eventResult.collect { event ->
                when (event) {
                    is ResultEvent.ShowError -> {
                        loadingStop()
                        showToast(event.errorMsg)
                    }
                    is ResultEvent.ShowSuccess -> {
                        loadingStop()
                        showSuccess(event.message)
                        delay(1000)
                        startActivity(Intent(this@ChangePasswordActivity, LoginActivity::class.java).putExtra(EMAIL, email))
                        this@ChangePasswordActivity.finish()
                    }
                }
            }
        }
    }



    private fun loadingStart(): Unit = runOnUiThread{ loading.loadingStart() }
    private fun loadingStop(): Unit = runOnUiThread{ loading.loadingDismiss() }
    private fun showToast(message: String): Unit = runOnUiThread{
        Toast.makeText(this@ChangePasswordActivity, message, Toast.LENGTH_LONG).show()
    }
    private fun showSuccess(message: String): Unit = runOnUiThread{
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}