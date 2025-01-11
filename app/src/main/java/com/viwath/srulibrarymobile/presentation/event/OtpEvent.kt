/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

sealed class OtpEvent {
    data object StartLoading : OtpEvent()
    data object StopLoading : OtpEvent()
    data object RestartCountdown : OtpEvent()
    data object VerifyNavigate : OtpEvent()
    data object ChangePasswordNavigate : OtpEvent()
    data class RequestOtp(val email: String): OtpEvent()
    data class VerifyOtp(val otp: String): OtpEvent()
}