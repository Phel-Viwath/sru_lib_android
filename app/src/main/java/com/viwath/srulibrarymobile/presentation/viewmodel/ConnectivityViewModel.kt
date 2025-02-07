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
import com.viwath.srulibrarymobile.utils.connectivity.NetworkConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ConnectivityViewModel] is a [ViewModel] responsible for observing and managing the device's network connectivity status.
 *
 * It utilizes a [NetworkConnectivityObserver] to detect changes in network availability and exposes the current status
 * and any relevant messages to the UI via [LiveData].
 *
 * @property connectivityObserver An instance of [NetworkConnectivityObserver] used to observe network changes.
 * @constructor Creates a [ConnectivityViewModel] with the provided [NetworkConnectivityObserver].
 */
@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    private val connectivityObserver: NetworkConnectivityObserver
): ViewModel(){

    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean> = _networkStatus

    private val _networkMessage = MutableLiveData<String?>()
    val networkMessage: LiveData<String?> get() = _networkMessage

    private var firstCheck = true
    private var previousStatus: Boolean? = null


    init {
        viewModelScope.launch{
            connectivityObserver.networkObserve().collect{ connected ->
                if (firstCheck){
                    firstCheck = false
                }
                else{
                    if (previousStatus != null && previousStatus != connected){
                        if (connected){
                            _networkMessage.value = "Connected"
                        }
                        else{
                            _networkMessage.value = "No Internet Connection"
                        }
                    }
                }
                previousStatus = connected
                _networkStatus.postValue(connected)
            }
        }
    }

    fun onSnackBarShown(){
        _networkMessage.value = null
    }

}