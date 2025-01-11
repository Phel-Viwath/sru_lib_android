/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viwath.srulibrarymobile.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val tokenManager: TokenManager
): ViewModel(){

    private val _username = MutableLiveData(tokenManager.getUsername() ?: "User")
    val username: LiveData<String> = _username
    private val _userType = MutableLiveData(tokenManager.getRole() ?: "?")
    val userType: LiveData<String> = _userType

    fun logout(){
        tokenManager.clearToken()
    }

}