/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.utils.connectivity.ConnectivityObserver
import com.viwath.srulibrarymobile.utils.connectivity.NetworkConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    private val connectivityObserver: NetworkConnectivityObserver
): ViewModel(){

    private val _networkState = MutableStateFlow(ConnectivityObserver.Status.Unavailable)
    val networkState: StateFlow<ConnectivityObserver.Status> get() = _networkState

    init {
        observerNetworkState()
    }

    private fun observerNetworkState() {
        viewModelScope.launch{
            connectivityObserver.observe().collect{ state ->
                _networkState.value = state
            }
        }
    }

}