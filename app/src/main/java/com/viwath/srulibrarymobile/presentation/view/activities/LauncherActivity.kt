/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.activities

import android.app.Dialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.databinding.ActivitySplashBinding
import com.viwath.srulibrarymobile.presentation.viewmodel.AuthViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.PinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * LauncherActivity is the initial activity that the user sees when the application starts.
 * It handles the authentication process and network connectivity checks before directing the user
 * to either the MainActivity or LoginActivity.
 *
 * This activity performs the following key functions:
 * 1. **Network Connectivity Check:** Determines if the device is connected to the internet.
 * 2. **Authentication:** Attempts to authenticate the user using the AuthViewModel.
 * 3. **Result Handling:** Processes the result of the authentication and navigates accordingly.
 * 4. **Error Handling:** Displays error messages via alert dialogs or snackbars.
 *
 * Uses the following dependencies:
 * - `AuthViewModel`: Responsible for handling authentication logic.
 * - `ConnectivityViewModel`: Responsible for observing network status.
 * - `ActivitySplashBinding`: View binding for the activity's layout.
 * - `Hilt`: For dependency injection.
 * - `Coroutines`: For asynchronous tasks.
 *
 */
@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val viewModel: AuthViewModel by viewModels()
    private val connectivityViewModel: ConnectivityViewModel by viewModels()
    private val pinViewModel: PinViewModel by viewModels()

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectivityViewModel.networkStatus.observe(this){ isConnected ->
            if (isConnected){
                viewModel.authenticate()
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.authResult.collect { result ->
                        withContext(Dispatchers.Main){
                            handleAuthResult(result)
                        }
                    }
                }
            }
            else{
                Snackbar.make(binding.root, "No Internet Connection.", Snackbar.LENGTH_SHORT).show()
            }
        }


    }

    override fun onStart() {
        super.onStart()
        if (!isNetworkConnected()){
            Snackbar.make(binding.root, "No Internet Connection.", Snackbar.ANIMATION_MODE_FADE).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
        dialog = null
    }

    private fun handleAuthResult(result: AuthResult<Unit>) {
        when (result) {
            is AuthResult.UnknownError -> {
                // Show an error message or handle the error as needed
                runOnUiThread {
                    alertDialog("Server problem")
                }
            }
            is AuthResult.Unauthorized, is AuthResult.InternalServerError, is AuthResult.BadRequest -> {
                // Navigate to LoginActivity if authentication fails
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            is AuthResult.Authorize -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                //checkUserRolePin()
            }
        }
    }

    private fun alertDialog(message: String){
        val dialog = MaterialAlertDialogBuilder(this)
            .setCancelable(true)
            .setTitle("System")
            .setMessage(message)
            .setPositiveButton("Ok"){dialog, _ -> dialog.dismiss() }
            .create()
        this.dialog = dialog
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(R.color.light_green))
    }


    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun checkUserRolePin(){
        val role = viewModel.role.value!!

        if (role == "ADMIN" || role == "SUPER_ADMIN"){
            if (pinViewModel.isPinSet()){
                navigateToMain()
            }
            else{
                startActivity(Intent(this, CreatePinActivity::class.java))
            }
        }else{
            navigateToMain()
        }
    }

    private fun navigateToMain(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}