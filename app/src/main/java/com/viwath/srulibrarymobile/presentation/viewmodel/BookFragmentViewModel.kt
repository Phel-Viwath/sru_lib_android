package com.viwath.srulibrarymobile.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.BookUseCase
import com.viwath.srulibrarymobile.presentation.state.book_state.SummaryBookState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookFragmentViewModel @Inject constructor(
    private val useCase: BookUseCase
): ViewModel(){

    private val _state = MutableStateFlow(SummaryBookState())
    val state: StateFlow<SummaryBookState> get() = _state.asStateFlow()

    init {
        getSummaryBook()
    }

    private fun getSummaryBook(){
        viewModelScope.launch {
            useCase.getSummaryUseCase().collect{ result ->
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