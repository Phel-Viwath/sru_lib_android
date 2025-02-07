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
import com.viwath.srulibrarymobile.domain.usecase.donation_usecase.DonationUseCase
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.book_state.DonationState
import com.viwath.srulibrarymobile.utils.updateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationTabViewModel @Inject constructor(
    private val useCase: DonationUseCase
): ViewModel(){

    private val _state = MutableStateFlow(DonationState())
    val state: StateFlow<DonationState> get() = _state

    private val _resultEvent = MutableSharedFlow<ResultEvent>()
    val resultEvent: SharedFlow<ResultEvent> get() = _resultEvent

    init {
        viewModelScope.launch{
            loadInitData()
        }
    }

    suspend fun loadInitData() = coroutineScope{
        val donationDeferred = async{ loadDonationList() }
        donationDeferred.await()
    }

    private fun emitEvent(event: ResultEvent){
        viewModelScope.launch {
            _resultEvent.emit(event)
        }
    }

    private fun loadDonationList(){
        viewModelScope.launch{
            useCase.getAllDonationUseCase().collect{
                when(it){
                    is Resource.Loading<*> -> {
                        _state.updateState { copy(isLoading = true) }
                    }
                    is Resource.Error<*> -> {
                        _state.updateState { copy(isLoading = false) }
                        emitEvent(ResultEvent.ShowError(it.message!!))
                    }
                    is Resource.Success<*> -> {
                        _state.updateState { copy(isLoading = false) }
                        _state.updateState { copy(donationList = it.data!!) }
                    }
                }
            }
        }
    }


}