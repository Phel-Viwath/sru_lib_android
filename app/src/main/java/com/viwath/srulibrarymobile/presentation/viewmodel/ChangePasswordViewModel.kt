/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.AuthUseCase
import com.viwath.srulibrarymobile.presentation.event.ChangePasswordEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.ChangePasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ChangePasswordViewModel]
 *
 * This ViewModel handles the logic for the change password screen. It interacts with the [AuthUseCase]
 * to perform the password change operation and manages the state of the UI.
 *
 * It exposes two main flows:
 *  - `state`: A [MutableStateFlow] that holds the current state of the UI, including input fields and loading state.
 *  - `eventResult`: A [SharedFlow] that emits events to the UI, such as success or error messages.
 *
 *  The view model also contain a method called [onEvent] that handle [ChangePasswordEvent] sent from UI.
 *
 * @property authUseCase An instance of [AuthUseCase] used for password change operations.
 */
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): ViewModel(){

    private val _state = MutableStateFlow(ChangePasswordState())
    val state: MutableStateFlow<ChangePasswordState> get() = _state

    private val _eventResult = MutableSharedFlow<ResultEvent>()
    val eventResult: SharedFlow<ResultEvent> get() = _eventResult.asSharedFlow()

    fun onEvent(event: ChangePasswordEvent){
        when(event){
            is ChangePasswordEvent.OnChangePassword -> changePassword()
            is ChangePasswordEvent.NewPasswordChange -> _state.value = _state.value.copy(newPassword = event.newPassword)
            is ChangePasswordEvent.ConfirmPasswordChange -> _state.value = _state.value.copy(confirmPassword = event.confirmPassword)
            is ChangePasswordEvent.EmailChange -> _state.value = _state.value.copy(email = event.email)
        }
    }

    private fun changePassword() {
        val newPassword = state.value.newPassword
        val confirmPassword = state.value.confirmPassword
        val email = state.value.email

        viewModelScope.launch{
            if (confirmPassword != newPassword) {
                _eventResult.emit(ResultEvent.ShowError("Passwords do not match."))
                return@launch
            }
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                _eventResult.emit(ResultEvent.ShowError("Password fields cannot be empty."))
                return@launch
            }
            authUseCase.changePasswordUseCase(email, newPassword).collect{ resource ->
                when(resource){
                    is Resource.Loading<*> -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Error<*> -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _eventResult.emit(ResultEvent.ShowError(resource.message ?: "An unknown error occurred"))
                    }
                    is Resource.Success<*> -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _eventResult.emit(ResultEvent.ShowSuccess("${resource.data}"))
                    }
                }
            }
        }
    }


}