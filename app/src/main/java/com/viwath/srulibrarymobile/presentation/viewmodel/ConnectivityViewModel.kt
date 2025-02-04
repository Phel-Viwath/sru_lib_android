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
import com.viwath.srulibrarymobile.utils.connectivity.ConnectivityObserver
import com.viwath.srulibrarymobile.utils.connectivity.NetworkConnectivityObserver
import com.viwath.srulibrarymobile.utils.connectivity.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    private val connectivityObserver: NetworkConnectivityObserver
): ViewModel(){

    private val _networkStatus = MutableLiveData<Status>()
    val networkStatus: LiveData<Status> = _networkStatus

    init {
        viewModelScope.launch{
            connectivityObserver.networkObserve().collect{ status ->
                _networkStatus.value = status
            }
        }
    }


}