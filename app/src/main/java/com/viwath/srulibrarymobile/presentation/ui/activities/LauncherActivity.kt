package com.viwath.srulibrarymobile.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Log.d("LauncherActivity", "onCreate started")

        viewModel.authenticate()

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.authResult.collect { result ->
                withContext(Dispatchers.Main) {
                    Log.d("Launcher", "onCreate: $result")
                    handleAuthResult(result)
                }
            }
        }

    }

    private fun handleAuthResult(result: AuthResult<Unit>) {
        when (result) {
            is AuthResult.UnknownError -> {
                // Show an error message or handle the error as needed
                AlertDialog.Builder(this)
                    .setMessage("Server problem")
                    .setTitle("System")
                    .setPositiveButton("Ok"){dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
            is AuthResult.Unauthorized, is AuthResult.InternalServerError, is AuthResult.BadRequest -> {
                // Navigate to LoginActivity if authentication fails
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            is AuthResult.Authorize -> {
                // Navigate to MainActivity if authentication succeeds
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }


        }
    }

}