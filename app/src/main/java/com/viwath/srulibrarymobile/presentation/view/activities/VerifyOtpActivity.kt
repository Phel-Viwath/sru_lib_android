/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.ActivityVerifyOtpBinding
import com.viwath.srulibrarymobile.presentation.event.OtpEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.viewmodel.OtpViewModel
import com.viwath.srulibrarymobile.utils.IntentString.EMAIL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyOtpActivity : AppCompatActivity(){
    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var loading: Loading

    private val viewModel: OtpViewModel by viewModels()

    private var countDownTimer: CountDownTimer? = null
    private val countDownTimeMillis: Long = 1000 * 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loading = Loading(this)

        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.line.setBackgroundColor(if (isDarkMode) getColor(R.color.white) else getColor(R.color.dark_blue))

        // get email from intend
        val email = intent.getStringExtra(EMAIL)
        if (email == null){
            showToast("Something went wrong.")
            return
        }

        binding.btVerifyOtp.setOnClickListener{
            val otp = binding.edtOtp.text.toString().trim()
            if (otp.isNotEmpty())
                viewModel.onEvent(OtpEvent.VerifyOtp(otp))
            else showToast("Please enter a valid email.")
        }
        binding.tvResentCode.isEnabled = false
        binding.tvResentCode.setOnClickListener{
            viewModel.onEvent(OtpEvent.RequestOtp(email))
        }
        countdownOtpExp()
        observeResultEvent()
        observeOtpEvent(email)
    }// onCreate function

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    // observe event function for display result
    private fun observeResultEvent(){
        lifecycleScope.launch{
            viewModel.event.collect{event ->
                when(event){
                    is ResultEvent.ShowError -> {showToast(event.errorMsg)}
                    is ResultEvent.ShowSuccess -> {showSnackbar(event.message)}
                }
            }
        }
    }

    // observe event function for display otp
    private fun observeOtpEvent(email: String){
        lifecycleScope.launch{
            viewModel.otpEvent.collect{ event ->
                when(event){
                    is OtpEvent.ChangePasswordNavigate -> {
                        stopLoading()
                        startActivity(
                            Intent(this@VerifyOtpActivity, ChangePasswordActivity::class.java)
                                .putExtra(EMAIL, email)
                        )
                        this@VerifyOtpActivity.finish()
                    }
                    is OtpEvent.StartLoading -> showLoading()
                    is OtpEvent.StopLoading -> stopLoading()
                    is OtpEvent.RestartCountdown -> countdownOtpExp()
                    else -> stopLoading()
                }
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread{
            Toast.makeText(this@VerifyOtpActivity, message, Toast.LENGTH_LONG).show()
        }
    }
    private fun showSnackbar(message: String) {
        runOnUiThread{
            val rootLayout = binding.linearRootLayoutOtp
            Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show()
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

    @SuppressLint("SetTextI18n")
    private fun countdownOtpExp(){
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(countDownTimeMillis, 1000){
            override fun onTick(millisUntilFinished: Long) {
                binding.tvExpDuration.text = "OTP code will expire in: ${millisUntilFinished/1000}"
            }
            override fun onFinish() {
                binding.tvExpDuration.text = "OTP code will expire in: 0"
                binding.tvResentCode.isEnabled = true
            }
        }
        countDownTimer?.start()
    }
}