package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viwath.srulibrarymobile.data.repository.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val tokenManager: TokenManager
): ViewModel(){

    private val _username = MutableLiveData("USER")
    val username: LiveData<String> = _username
    private val _userType = MutableLiveData("USER")
    val userType: LiveData<String> = _userType

    private val _isDarkModeEnable = MutableLiveData(false)
    val isDarkModeEnable: LiveData<Boolean> = _isDarkModeEnable

}