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
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.BookUseCase
import com.viwath.srulibrarymobile.presentation.state.book_state.SummaryBookState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for the Book Fragment, responsible for managing and providing data
 * related to book summaries.
 *
 * This ViewModel interacts with the [BookUseCase] to fetch and update book summary information.
 * It exposes a [StateFlow] ([state]) to the UI, allowing for reactive updates
 * based on changes in the summary data.
 *
 * @property useCase An instance of [BookUseCase] to interact with the data layer for book summary operations.
 */
@HiltViewModel
class BookFragmentViewModel @Inject constructor(
    private val useCase: BookUseCase
): ViewModel(){

    private val _state = MutableStateFlow(SummaryBookState())
    val state: StateFlow<SummaryBookState> get() = _state.asStateFlow()

    init {
        viewModelScope.launch{
            val summaryBookDeferred = async{ getSummaryBook() }
            summaryBookDeferred.await()
        }
    }

    private suspend fun getSummaryBook(){
        withContext(IO) {
            val summaryResult = async{
                useCase.getSummaryUseCase()
            }.await()
            summaryResult.collect{ result ->
                when(result){
                    is Resource.Success -> {
                        val data = result.data
                        if (data != null){
                            _state.value = _state.value.copy(
                                isLoading = false,
                                totalBook = data.totalBook,
                                totalDonation = data.totalDonation,
                                totalBorrowed = data.totalBorrow,
                                totalExpiration = data.totalExp,
                                borrowToday = data.todayBorrowed,
                                returnToday = data.todayReturned
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            error = result.message ?: "An unknown error occurred",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

}