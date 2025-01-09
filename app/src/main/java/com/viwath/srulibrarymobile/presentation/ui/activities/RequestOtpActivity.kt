package com.viwath.srulibrarymobile.presentation.ui.activities

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
import com.viwath.srulibrarymobile.presentation.viewmodel.RequestOtpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RequestOtpActivity : AppCompatActivity(){

    private lateinit var binding: ActivityRequestOtpBinding
    private lateinit var loading: Loading

    private val viewModel: RequestOtpViewModel by lazy {
        ViewModelProvider(this)[RequestOtpViewModel::class.java]
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
            val email = binding.edtEmail.text.toString().trim()
            if (email.isNotEmpty())
                viewModel.onEvent(OtpEvent.RequestOtp(email))
            else showToast("Please enter a valid email.")

        }
        binding.btVerify.setOnClickListener{
            val otp = binding.edtOtp.text.toString().trim()
            if (otp.isNotEmpty())
                viewModel.onEvent(OtpEvent.VerifyOtp(otp))
            else showToast("Please enter a valid OTP.")

        }

        // event
        observeEvent()
        //state
        observeOtpEvent()

    }

    private suspend fun showLoading(){
        withContext(Main){
            loading.loadingStart()
        }
    }
    private suspend fun stopLoading(){
        withContext(Main){
            loading.loadingDismiss()
        }
    }

    private fun observeOtpEvent(){
        lifecycleScope.launch{
            viewModel.otpEvent.collect{ event ->
                when(event){
                    is OtpEvent.StartLoading -> showLoading()
                    is OtpEvent.StopLoading -> stopLoading()
                    is OtpEvent.Navigate -> {
                        stopLoading()
                        startActivity(Intent(this@RequestOtpActivity, ChangePasswordActivity::class.java))
                        this@RequestOtpActivity.finish()
                    }
                    else -> stopLoading()
                }
            }
        }
    }

    private fun observeEvent() {
        lifecycleScope.launch {
            viewModel.event.collect { event ->
                when (event) {
                    is ResultEvent.ShowSnackbar -> showSnackbar(event.message)
                    is ResultEvent.ShowError -> showToast(event.errorMsg)
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        lifecycleScope.launch(Main){
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showToast(message: String) {
        lifecycleScope.launch(Main){
            Toast.makeText(this@RequestOtpActivity, message, Toast.LENGTH_LONG).show()
        }
    }

}