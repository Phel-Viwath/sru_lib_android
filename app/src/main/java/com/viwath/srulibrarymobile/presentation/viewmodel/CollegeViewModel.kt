/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.domain.usecase.GetCollegeUseCase
import com.viwath.srulibrarymobile.presentation.state.CollegeState
import com.viwath.srulibrarymobile.utils.collectResource
import com.viwath.srulibrarymobile.utils.updateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollegeViewModel @Inject constructor(
    private val useCase: GetCollegeUseCase
): ViewModel(){

    private val _state = MutableStateFlow(CollegeState())
    val state: StateFlow<CollegeState> get() = _state

    init {
        viewModelScope.launch{
            val collegeDeffer = async { loadCollege() }
            collegeDeffer.await()
        }
    }

    private suspend fun loadCollege(){
        useCase.invoke().collectResource(
            onLoading = { _state.updateState { copy(isLoading = true) }},
            onSuccess = {college -> _state.updateState { copy(isLoading = false, colleges = college) }},
            onError = {error -> _state.updateState { copy(isLoading = false, error = error) }}
        )
    }

}