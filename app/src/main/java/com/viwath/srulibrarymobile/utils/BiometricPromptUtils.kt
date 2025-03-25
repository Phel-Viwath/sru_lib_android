/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class BiometricPromptUtils(
    private val activity: AppCompatActivity
): DefaultLifecycleObserver {

    private val _biometricResultChannel = MutableSharedFlow<BiometricResult>(
        replay = 1,
        extraBufferCapacity = 1
    )
    val biometricResultFlow: SharedFlow<BiometricResult> get() = _biometricResultChannel

    private var currentPrompt: BiometricPrompt? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        cancelAuthentication()
    }

    fun showBiometricForm(){
        currentPrompt?.cancelAuthentication()
        val manager = BiometricManager.from(activity)
        val authenticators = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        }else BIOMETRIC_STRONG

        val promptInfo = PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setDescription("Verify your identity using biometrics")
            .setAllowedAuthenticators(authenticators)
            .apply {
                if (Build.VERSION.SDK_INT < 30){
                    setNegativeButtonText("Cancel")
                }
            }
            .build()

        when(manager.canAuthenticate(authenticators)){
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                _biometricResultChannel.tryEmit(BiometricResult.FeatureUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                _biometricResultChannel.tryEmit(BiometricResult.HardwareUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                _biometricResultChannel.tryEmit(BiometricResult.AuthenticationNotSet)
                return
            }
            else -> Unit
        }

        currentPrompt = createPrompt()
        currentPrompt?.authenticate(promptInfo)
    }

    private fun createPrompt(): BiometricPrompt = BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(activity),
        object : AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                val result = when(errorCode){
                    BiometricPrompt.ERROR_CANCELED -> BiometricResult.AuthenticationCanceled
                    else -> BiometricResult.AuthenticationError(errString.toString())
                }
                _biometricResultChannel.tryEmit(result)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                _biometricResultChannel.tryEmit(BiometricResult.AuthenticationSucceeded)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                _biometricResultChannel.tryEmit(BiometricResult.AuthenticationFail)
            }
        }
    )

    private fun cancelAuthentication() {
        currentPrompt?.cancelAuthentication()
        currentPrompt = null
    }

    sealed interface BiometricResult{
        data object HardwareUnavailable: BiometricResult
        data object FeatureUnavailable: BiometricResult
        data object AuthenticationSucceeded: BiometricResult
        data object AuthenticationNotSet: BiometricResult
        data object AuthenticationFail: BiometricResult
        data object AuthenticationCanceled: BiometricResult
        data object NoDeviceCredentialIsSet: BiometricResult
        data class AuthenticationError(val error: String): BiometricResult
    }

    companion object{
        private var biometricEnrollmentRequested = false

        fun requestBiometricEnrollment(onIntent: (Intent) -> Unit){
            if (!biometricEnrollmentRequested) {
                biometricEnrollmentRequested = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                    onIntent(enrollIntent)
                }else{
                    val enrollIntent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                    onIntent(enrollIntent)
                }
            }
        }

        fun resetBiometricEnrollmentRequest() {
            biometricEnrollmentRequested = false // Reset when user returns to app
        }
    }
}