package com.viwath.srulibrarymobile.presentation.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.presentation.event.AuthEvent
import com.viwath.srulibrarymobile.presentation.viewmodel.AuthViewModel
import com.viwath.srulibrarymobile.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(){

    /// global variable or object
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val nightButtonBackground = R.drawable.night_button_bg
        val lightButtonBackground = R.drawable.light_button_bg
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.btLogin.setBackgroundResource(if(isDarkMode) nightButtonBackground else lightButtonBackground)


        binding.tvGoSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        val state = viewModel.state

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.authResult.collect {result ->
                when(result){
                    is AuthResult.Unauthorized -> {
                        dialogMessage(
                            "Please check your email. ${result.data}",
                            "Unauthorized"
                        )
                    }
                    is AuthResult.Authorize -> {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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

        binding.edtUsername.setText(state.value.signInEmail)
        binding.edtPassword.setText(state.value.signInPassword)

        binding.btLogin.setOnClickListener {
            val username = binding.edtUsername.text
            val password = binding.edtPassword.text

            viewModel.onEvent(AuthEvent.SignInUsernameChanged(username.toString()))
            viewModel.onEvent(AuthEvent.SignInPasswordChanged(password.toString()))
            viewModel.onEvent(AuthEvent.SignIn)
        }

        lifecycleScope.launch {
            viewModel.exceptionFlow.collect{ message ->
                dialogMessage(message, "Error!")
            }
        }

    }

    private fun dialogMessage(message: String, title: String){
        runOnUiThread {
            if(!isFinishing && !isDestroyed)
                AlertDialog.Builder(this)
                    .setMessage(message)
                    .setTitle(title)
                    .setPositiveButton("OK"){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }
    }

}