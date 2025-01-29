/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow
import com.viwath.srulibrarymobile.domain.usecase.borrow_usecase.BorrowUseCase
import com.viwath.srulibrarymobile.presentation.event.BorrowedTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.book_state.BorrowState
import com.viwath.srulibrarymobile.utils.DateTimeUtil.borrowOverDueDate
import com.viwath.srulibrarymobile.utils.DateTimeUtil.toLocalDate
import com.viwath.srulibrarymobile.utils.collectResource
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
class BorrowTabViewModal @Inject constructor(
    private val useCase: BorrowUseCase
): ViewModel(){

    private val _borrowList: MutableList<Borrow> = mutableListOf()

    private val _state = MutableStateFlow(BorrowState())
    val state: StateFlow<BorrowState> get() = _state

    private val _resultEvent = MutableSharedFlow<ResultEvent>()
    val resultEvent: SharedFlow<ResultEvent> get() = _resultEvent

    init {
        viewModelScope.launch{
            loadInitData()
        }
    }

    fun onEvent(event: BorrowedTabEvent){
        when(event){
            is BorrowedTabEvent.ExtendBorrow -> {}
            is BorrowedTabEvent.FilterOverDue -> { filterBorrowList() }
            is BorrowedTabEvent.SearchBorrow -> {}

            is BorrowedTabEvent.OnSearchTextChange -> { _state.updateState { copy(searchKeywordChange = event.keyword) } }
            is BorrowedTabEvent.OnFilterChange -> { _state.updateState { copy(isFilter = event.isFilter) } }
        }
    }

    suspend fun loadInitData() = coroutineScope {
        val borrowDeferred = async{ loadBorrowList() }
        borrowDeferred.await()
    }

    private suspend fun loadBorrowList(){
        useCase.getBorrowsUseCase().collectResource(
            onLoading = { _state.updateState { copy(isLoading = true) }},
            onError = { message ->
                _state.updateState { copy(isLoading = false) }
                emitEvent(ResultEvent.ShowError(message))
            },
            onSuccess = { items ->
                _borrowList.clear()
                _borrowList.addAll(items)
                _state.updateState { copy(isLoading = false) }
                _state.updateState { copy(borrowList = items) }
            }
        )
    }

    private fun filterBorrowList(){
        val isFilter = _state.value.isFilter
        val borrowFiltered = _borrowList
            .filter { borrow ->
                borrowOverDueDate(borrow.borrowDate.toLocalDate()) && borrow.isBringBack == false
            }
        if (isFilter)
            _state.updateState { copy(borrowList = borrowFiltered) }
        else _state.updateState { copy(borrowList = _borrowList) }

    }


    private fun emitEvent(event: ResultEvent){
        viewModelScope.launch {
            _resultEvent.emit(event)
        }

    }


}