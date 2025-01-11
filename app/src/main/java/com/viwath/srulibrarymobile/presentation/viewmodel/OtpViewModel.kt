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
import com.viwath.srulibrarymobile.presentation.event.OtpEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): ViewModel(){
    private val _resultEvent = MutableSharedFlow<ResultEvent>()
    val event: SharedFlow<ResultEvent> get() = _resultEvent.asSharedFlow()

    private val _otpEvent = MutableSharedFlow<OtpEvent>()
    val otpEvent: SharedFlow<OtpEvent> get() = _otpEvent.asSharedFlow()

    fun onEvent(event: OtpEvent){
        when(event){
            is OtpEvent.RequestOtp -> {requestOtp(event.email)}
            is OtpEvent.VerifyOtp -> {verifyOtp(event.otp)}
            else -> Unit
        }
    }

    private fun requestOtp(email: String){
        viewModelScope.launch{
            authUseCase.requestOtpUseCase(email).collect{ resource ->
                when(resource) {
                    is Resource.Loading<*> -> _otpEvent.emit(OtpEvent.StartLoading)
                    is Resource.Error<*> -> {
                        _otpEvent.emit(OtpEvent.StopLoading)
                        _resultEvent.emit(ResultEvent.ShowError(resource.message ?: "Unknown error"))
                    }
                    is Resource.Success<*> -> {
                        _otpEvent.emit(OtpEvent.StopLoading)
                        _resultEvent.emit(ResultEvent.ShowSuccess("Request OTP successfully. Please check your email."))
                        delay(500)
                        _otpEvent.emit(OtpEvent.VerifyNavigate)
                        _otpEvent.emit(OtpEvent.RestartCountdown)
                    }
                }
            }
        }
    }

    private fun verifyOtp(otp: String){
        viewModelScope.launch{
            authUseCase.verifyOtpUseCase(otp).collect{ resource ->
                when(resource){
                    is Resource.Loading<*> -> _otpEvent.emit(OtpEvent.StartLoading)
                    is Resource.Error<*> -> {
                        _otpEvent.emit(OtpEvent.StopLoading)
                        _resultEvent.emit(ResultEvent.ShowError(resource.message ?: "Unknown error"))
                    }
                    is Resource.Success<*> -> {
                        _otpEvent.emit(OtpEvent.StopLoading)
                        _resultEvent.emit(ResultEvent.ShowSuccess("Confirm OTP successfully."))
                        delay(500)
                        _otpEvent.emit(OtpEvent.ChangePasswordNavigate)
                    }
                }
            }
        }
    }


}