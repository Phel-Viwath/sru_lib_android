/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viwath.srulibrarymobile.utils.KeyStoreManager
import com.viwath.srulibrarymobile.utils.share_preferences.PinEncryptionData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val keyStoreManager: KeyStoreManager,
    private val pinEncryptionData: PinEncryptionData
): ViewModel(){

    private val _pinStatus = MutableLiveData<Boolean>()
    val pinStatus: LiveData<Boolean> get() = _pinStatus

    fun savePin(pin: String){
        val encryptedPin = keyStoreManager.encrypt(pin.toByteArray())
        pinEncryptionData.savePin(encryptedPin)
    }

    fun validatePin(inputPin: String){
        val encryptedPinBase64 = pinEncryptionData.getEncryptedPin()
        val ivBase64 = pinEncryptionData.getEncryptionIv()
        if (encryptedPinBase64 != null && ivBase64 != null){
            val encryptedPin = Base64.decode(encryptedPinBase64, Base64.DEFAULT)
            val iv = Base64.decode(ivBase64, Base64.DEFAULT)

            val decryptedPin = keyStoreManager.decrypt(encryptedBytes = encryptedPin)
            _pinStatus.value = (String(decryptedPin) == inputPin)
        }
        else _pinStatus.value = false
    }

    fun isPinSet(): Boolean {
        return pinEncryptionData.getEncryptedPin() != null
    }

    fun clearPin(){
        pinEncryptionData.clearPin()
    }
}