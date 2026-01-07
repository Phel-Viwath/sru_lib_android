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
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.presentation.state.SettingState
import com.viwath.srulibrarymobile.utils.datastore.SettingPreferences
import com.viwath.srulibrarymobile.utils.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [SettingViewModel] is a [ViewModel] responsible for managing the state and logic related to the user's settings screen.
 *
 * It provides access to the user's username and user type, and allows the user to log out.
 *
 * @property userPreferences [UserPreferences] instance used for managing user authentication tokens and data.
 */
@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val settingPreferences: SettingPreferences
): ViewModel(){

    private val _state = MutableStateFlow(SettingState())
    val state: StateFlow<SettingState> get() = _state

    private val _viewMode = MutableLiveData<String>()
    val viewMode: LiveData<String> get() = _viewMode

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(username = userPreferences.getUserId() ?: "User")
            _state.value = _state.value.copy(userType = userPreferences.getRole() ?: "?")
            _state.value = _state.value.copy(themeMode = settingPreferences.getSavedTheme())
            _viewMode.postValue(settingPreferences.getViewMode())
        }
    }

    fun setTheme(themeMode: Int){
        viewModelScope.launch {
            settingPreferences.saveTheme(themeMode)
            _state.value = _state.value.copy(themeMode = settingPreferences.getSavedTheme())
        }
    }

    fun saveViewMode(viewMode: String){
        viewModelScope.launch {
            settingPreferences.saveViewMode(viewMode)
            _viewMode.postValue(settingPreferences.getViewMode())
        }
    }

    fun logout(){
        viewModelScope.launch {
            userPreferences.clearToken()
        }
    }

    companion object{
        const val CLASSIC = "classic"
        const val MODERN = "modern"
    }

}