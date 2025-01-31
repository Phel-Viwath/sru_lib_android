/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import android.util.Log
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
class BorrowTabViewModel @Inject constructor(
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
            is BorrowedTabEvent.ExtendBorrow -> { extendBorrow() }
            is BorrowedTabEvent.FilterOverDue -> { filterBorrowList() }
            is BorrowedTabEvent.SearchBorrow -> { searchBorrowList() }
            is BorrowedTabEvent.FilterByTextSearch -> { filterByTextChange() }
            is BorrowedTabEvent.GetAllBorrow -> { loadAllBorrows() }
            is BorrowedTabEvent.ReturnBook -> { returnBook() }

            is BorrowedTabEvent.OnSearchTextChange -> { _state.updateState { copy(searchKeywordChange = event.keyword) } }
            is BorrowedTabEvent.OnFilterChange -> { _state.updateState { copy(isFilter = event.isFilter) } }
            is BorrowedTabEvent.OnBorrowIdChange -> { _state.updateState { copy(borrowId = event.borrowId) }}
            is BorrowedTabEvent.OnReturnBookDataChange -> {_state.updateState { copy(studentId = event.studentId, bookId = event.bookId) }}
        }
    }

    suspend fun loadInitData() = coroutineScope {
        val borrowDeferred = async{ loadBorrowList() }
        borrowDeferred.await()
    }

    private fun emitEvent(event: ResultEvent){
        viewModelScope.launch {
            _resultEvent.emit(event)
        }
    }

    private fun loadAllBorrows(){
        viewModelScope.launch{
            useCase.getAllBorrowUseCase().collectResource(
                onLoading = { _state.updateState { copy(isLoading = true) }},
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    _state.updateState { copy(borrowList = it) }
                }
            )
        }
    }

    private fun filterByTextChange(){
        val keyword = _state.value.searchKeywordChange
        val filterList = _borrowList.filter {
            it.bookId.contains(keyword) || it.studentId.toString().contains(keyword) ||
                    it.borrowDate.contains(keyword) || it.studentName.contains(keyword) || it.bookTitle.contains(keyword)
        }
        _state.updateState { copy(borrowList = filterList) }
    }

    private suspend fun loadBorrowList(){
        useCase.getActiveBorrowsDetailUseCase().collectResource(
            onLoading = { _state.updateState { copy(isLoading = true) }},
            onError = { message ->
                _state.updateState { copy(isLoading = false) }
                emitEvent(ResultEvent.ShowError(message))
            },
            onSuccess = { items ->
                _state.updateState { copy(isLoading = false) }
                _borrowList.clear()
                _borrowList.addAll(items)
                _state.updateState { copy(borrowList = items) }
            }
        )
    }

    private fun filterBorrowList(){
        val isFilter = _state.value.isFilter
        val borrowFiltered = _borrowList
            .filter { borrow ->
                val borrowDate = borrow.borrowDate.toLocalDate()
                val isOverdue = borrowOverDueDate(borrowDate, borrow.isExtend)
                isOverdue && !borrow.isBringBack && !borrow.isExtend
            }
        val resultList = if (borrowFiltered.isEmpty()) {
            emptyList()
        } else {
            borrowFiltered
        }
        if (isFilter) _state.updateState { copy(borrowList = resultList) }
        else _state.updateState { copy(borrowList = _borrowList) }
    }

    private fun searchBorrowList(){
        val keyword = _state.value.searchKeywordChange
        viewModelScope.launch{
            useCase.searchBorrowUseCase(keyword).collectResource(
                onLoading = { _state.updateState { copy(isLoading = true) }},
                onError = { message ->
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(message))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    _state.updateState { copy(borrowList = it) }
                }
            )
        }
    }

    private fun extendBorrow(){
        val borrowId = _state.value.borrowId
        if (borrowId <= 0){
            emitEvent(ResultEvent.ShowError("Error data is empty."))
            return
        }
        viewModelScope.launch{
            useCase.extendBorrowUseCase(borrowId).collectResource(
                onLoading = { _state.updateState { copy(isLoading = true) }},
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Successfully."))
                }
            )
        }

    }

    private fun returnBook() {
        val bookId = _state.value.bookId
        val studentId = _state.value.studentId
        if (bookId.isEmpty() || studentId <= 0) {
            emitEvent(ResultEvent.ShowError("Error data is empty."))
            return
        }

        viewModelScope.launch{
            useCase.returnBookUseCase(studentId, bookId).collectResource(
                onLoading = {
                    _state.updateState { copy(isLoading = true) }
                },
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Success."))
                }
            )
        }

    }


}