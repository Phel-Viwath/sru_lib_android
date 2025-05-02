/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.domain.usecase.GetLanguageUseCase
import com.viwath.srulibrarymobile.presentation.state.LanguageState
import com.viwath.srulibrarymobile.utils.collectResource
import com.viwath.srulibrarymobile.utils.updateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val useCase: GetLanguageUseCase
): ViewModel(){

    private val _state = MutableStateFlow(LanguageState())
    val state: StateFlow<LanguageState> get() = _state

    init {
        viewModelScope.launch{
            val languageDeferred = async { loadLanguage() }
            languageDeferred.await()
        }
    }

    suspend fun loadLanguage(){
        useCase.invoke().collectResource(
            onLoading = { _state.updateState { copy(isLoading = true) }},
            onSuccess = {language -> _state.updateState { copy(isLoading = false, languages = language) }},
            onError = {error -> _state.updateState { copy(isLoading = false, error = error) }}
        )
    }

}