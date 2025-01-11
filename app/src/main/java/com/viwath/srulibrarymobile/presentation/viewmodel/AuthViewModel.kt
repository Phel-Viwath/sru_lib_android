/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.common.exception.InvalidAuthException
import com.viwath.srulibrarymobile.common.result.AuthResult
import com.viwath.srulibrarymobile.domain.model.auth.LogInRequest
import com.viwath.srulibrarymobile.domain.model.auth.RegisterRequest
import com.viwath.srulibrarymobile.domain.usecase.auth_usecase.AuthUseCase
import com.viwath.srulibrarymobile.presentation.event.AuthEvent
import com.viwath.srulibrarymobile.presentation.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val useCase: AuthUseCase
): ViewModel(){

    var state = MutableStateFlow(AuthState())
    var isLoading = MutableStateFlow(false)

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResult = resultChannel.receiveAsFlow()

    // exception chanel
    private val exceptionChanel = Channel<String>()
    val exceptionFlow = exceptionChanel.receiveAsFlow()


    fun onEvent(event: AuthEvent) {
        when(event) {

            is AuthEvent.SignUpUsernameChanged -> state.value = state.value.copy(signUpUsername = event.value)
            is AuthEvent.SignUpEmailChanged -> state.value = state.value.copy(signUpEmail = event.value)
            is AuthEvent.SignUpPasswordChanged -> state.value = state.value.copy(signUpPassword = event.value)
            is AuthEvent.SignUp -> register()

            // SignIn
            is AuthEvent.SignInUsernameChanged -> state.value = state.value.copy(signInEmail = event.value)
            is AuthEvent.SignInPasswordChanged -> state.value = state.value.copy(signInPassword = event.value)
            is AuthEvent.SignIn -> logIn()

        }
    }

    private fun logIn() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = async {
                    useCase.signinUseCase.invoke(
                        request = LogInRequest(
                            password = state.value.signInPassword,
                            email = state.value.signInEmail
                        )
                    )
                }.await()
                resultChannel.send(result)
            }catch (e: InvalidAuthException){
                exceptionChanel.send(e.message ?: "An unknown error occurred")
                Log.d("AuthViewModel", "logIn: ${e.message}")
            }finally {
                isLoading.value = false
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = async {
                    useCase.registerUseCase.invoke(
                        RegisterRequest(
                            username = state.value.signUpUsername,
                            password = state.value.signUpPassword,
                            email = state.value.signUpEmail
                        )
                    )
                }.await()
                resultChannel.send(result)
            }catch (e: InvalidAuthException){
                exceptionChanel.send(e.message ?: "An unknown error occurred")
            }finally {
                isLoading.value = false
            }
        }
    }

    fun authenticate() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = useCase.authenticateUseCase()
                resultChannel.send(result)
            } catch (e: Exception) {
                Log.e("AuthViewModel error", "authenticate() exception: ${e.message}", e)
            }
        }
    }

}