package com.viwath.srulibrarymobile.presentation.event

sealed class RequestOtpEvent {
    data object StartLoading : RequestOtpEvent()
    data object StopLoading : RequestOtpEvent()
    data object Navigate : RequestOtpEvent()
    data class RequestRequestOtp(val email: String): RequestOtpEvent()
}