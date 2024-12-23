package com.viwath.srulibrarymobile.presentation.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.SpannableString
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.presentation.event.AuthEvent
import com.viwath.srulibrarymobile.presentation.viewmodel.AuthViewModel
import com.viwath.srulibrarymobile.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity(){

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nightButtonBackground = R.drawable.night_button_bg
        val lightButtonBackground = R.drawable.light_button_bg
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.btRegister.setBackgroundResource(if(isDarkMode) nightButtonBackground else lightButtonBackground)

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
            finish()
        }

        val state = viewModel.state

        CoroutineScope(Dispatchers.IO).launch {
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
                        finish()
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

        lifecycleScope.launch {
            viewModel.exceptionFlow.collect{ message ->
                dialogMessage(message = message, title = "Error!")
            }
        }
    }

    private fun dialogMessage(message: String, title: String){
        runOnUiThread {
            if (!isFinishing && !isDestroyed)
                AlertDialog.Builder(this)
                    .setMessage(message)
                    .setTitle(title)
                    .setPositiveButton("OK"){ dialog, _ ->
                        dialog.dismiss()
                    }.create()
                    .show()
        }
    }
}