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
import com.viwath.srulibrarymobile.utils.connectivity.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()
    private val connectivityViewModel: ConnectivityViewModel by viewModels()
    private lateinit var binding: ActivitySplashBinding
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectivityViewModel.networkStatus.observe(this){ status ->
            when(status){
                Status.DISCONNECTED -> alertDialog("No Internet Connection")
                Status.CONNECTED -> {
                    viewModel.authenticate()
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.authResult.collect { result ->
                            withContext(Dispatchers.Main) {
                                handleAuthResult(result)
                            }
                        }
                    }
                }
                Status.CONNECTED_WIFI -> {}
                Status.CONNECTED_MOBILE -> {}
                Status.CONNECTED_UNMETERED -> {}
            }
        }


    }

    override fun onStart() {
        super.onStart()
        if (!isNetworkConnected()){
            alertDialog("No Internet Connection")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

    private fun handleAuthResult(result: AuthResult<Unit>) {
        when (result) {
            is AuthResult.UnknownError -> {
                // Show an error message or handle the error as needed
                alertDialog("Server problem")
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

    private fun snackbar(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}