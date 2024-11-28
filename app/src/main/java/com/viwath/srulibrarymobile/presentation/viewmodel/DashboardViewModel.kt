package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.common.result.CoreResult
import com.viwath.srulibrarymobile.utils.TokenManager
import com.viwath.srulibrarymobile.domain.usecase.dashboard_usecase.DashboardUseCase
import com.viwath.srulibrarymobile.presentation.state.DashboardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val useCase: DashboardUseCase,
    tokenManager: TokenManager
): ViewModel(){

    private val _state = MutableLiveData(DashboardState())
    val state: LiveData<DashboardState> = _state
    val username = tokenManager.getUsername()

    init {
        if(_state.value?.dashboard == null){
            getDashboard()
        }
    }

    fun getDashboard() {
        useCase().onEach { result ->
            when(result){
                is CoreResult.Success -> {
                    _state.value = DashboardState(dashboard = result.data)
                }
                is CoreResult.Loading -> {
                    _state.value = DashboardState(isLoading = true)
                }
                is CoreResult.Error -> {
                    _state.value = DashboardState(error = result.message.toString())
                }
            }
        }.launchIn(viewModelScope)
    }
}