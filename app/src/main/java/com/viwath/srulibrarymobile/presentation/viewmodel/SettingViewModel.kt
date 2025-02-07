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

/**
 * [SettingViewModel] is a [ViewModel] responsible for managing the state and logic related to the user's settings screen.
 *
 * It provides access to the user's username and user type, and allows the user to log out.
 *
 * @property tokenManager [TokenManager] instance used for managing user authentication tokens and data.
 */
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