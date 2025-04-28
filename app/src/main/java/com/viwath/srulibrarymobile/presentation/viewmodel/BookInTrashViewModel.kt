/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.BookUseCase
import com.viwath.srulibrarymobile.presentation.event.InTrashEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.InTrashState
import com.viwath.srulibrarymobile.utils.collectResource
import com.viwath.srulibrarymobile.utils.updateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookInTrashViewModel @Inject constructor(
    private val useCase: BookUseCase
): ViewModel(){

    private val _state = MutableStateFlow(InTrashState())
    val state: StateFlow<InTrashState> get() = _state

    private val _resultEvent = MutableSharedFlow<ResultEvent>()
    val resultEvent: SharedFlow<ResultEvent> get() = _resultEvent

    init {
        viewModelScope.launch {
            val bookInTrashDeferred = async{ loadBookInTrash() }
            bookInTrashDeferred.await()
        }
    }

    fun onEvent(event: InTrashEvent){
        when(event){
            is InTrashEvent.RestoreBook -> restoreBook()
            is InTrashEvent.SearchInTrash -> {}
            is InTrashEvent.DeleteBook -> deleteBook()
            is InTrashEvent.FilterByTextSearch -> {}
            is InTrashEvent.RefreshData -> {
                viewModelScope.launch {
                    loadBookInTrash()
                }
            }

            is InTrashEvent.OnDeleteClicked -> _state.updateState { copy(bookId = event.bookId) }
            is InTrashEvent.OnRestoreClicked -> _state.updateState { copy(bookId = event.bookId) }
        }
    }



    private fun emitEvent(event: ResultEvent){
        viewModelScope.launch {
            _resultEvent.emit(event)
        }
    }

    private suspend fun loadBookInTrash(){
        useCase.getBookInTrashUseCase().collectResource(
            onLoading = { _state.updateState { copy(isLoading = true) }},
            onError = {
                _state.updateState { copy(isLoading = false) }
                emitEvent(ResultEvent.ShowError(it))
            },
            onSuccess = {
                _state.updateState { copy(isLoading = false, booksInTrash = it) }
            }
        )
    }

    private fun deleteBook(){
        val bookId = _state.value.bookId
        viewModelScope.launch {
            useCase.deleteBookUseCase(bookId).collectResource(
                onLoading = { _state.updateState { copy(isLoading = true) }},
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Book deleted"))
                }
            )
        }
    }

    private fun restoreBook(){
        val bookId = _state.value.bookId
        viewModelScope.launch {
            useCase.recoverBookUseCase(bookId).collectResource(
                onLoading = {
                    _state.updateState { copy(isLoading = true) }
                },
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Book restored"))
                }
            )
        }
    }

}