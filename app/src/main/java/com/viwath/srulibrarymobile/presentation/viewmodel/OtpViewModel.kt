package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.AuthUseCase
import com.viwath.srulibrarymobile.presentation.event.RequestOtpEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestOtpViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): ViewModel(){
    private val _resultEvent = MutableSharedFlow<ResultEvent>()
    val event: SharedFlow<ResultEvent> get() = _resultEvent.asSharedFlow()

    private val _requestEvent = MutableSharedFlow<RequestOtpEvent>()
    val requestEvent: SharedFlow<RequestOtpEvent> get() = _requestEvent.asSharedFlow()

    fun onEvent(event: RequestOtpEvent){
        when(event){
            is RequestOtpEvent.RequestRequestOtp -> {requestOtp(event.email)}
            else -> Unit
        }
    }

    private fun requestOtp(email: String){
        viewModelScope.launch{
            authUseCase.requestOtpUseCase(email).collect{ resource ->
                when(resource) {
                    is Resource.Loading<*> -> _requestEvent.emit(RequestOtpEvent.StartLoading)
                    is Resource.Error<*> -> {
                        _requestEvent.emit(RequestOtpEvent.StopLoading)
                        _resultEvent.emit(ResultEvent.ShowError(resource.message ?: "Unknown error"))
                    }
                    is Resource.Success<*> -> {
                        _requestEvent.emit(RequestOtpEvent.StopLoading)
                        _requestEvent.emit(RequestOtpEvent.Navigate)
                        _resultEvent.emit(ResultEvent.ShowSnackbar("Request OTP successfully. Please check your email."))
                    }
                }
            }
        }
    }


}