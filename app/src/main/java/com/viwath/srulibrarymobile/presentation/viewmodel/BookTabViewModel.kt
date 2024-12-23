package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.BookUseCase
import com.viwath.srulibrarymobile.presentation.event.BookTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.book_state.BookTabState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookTabViewModel @Inject constructor(
    private val useCase: BookUseCase
): ViewModel(){
    private val _state = MutableStateFlow(BookTabState())
    val state: StateFlow<BookTabState> get() = _state

    private val _eventFlow = MutableSharedFlow<ResultEvent>()
    val eventFlow: SharedFlow<ResultEvent> get() = _eventFlow.asSharedFlow()

    init {
        loadListBook()
    }

    fun onEvent(event: BookTabEvent){
        when(event){
            is BookTabEvent.SaveBook -> { saveBook(event.book) }
            is BookTabEvent.Remove -> { removeBook(event.bookId) }
            is BookTabEvent.UpdateBook -> {}
            is BookTabEvent.Borrow -> { }
        }
    }

    private fun loadListBook(){
        viewModelScope.launch {
            useCase.getBooksUseCase().collect{
                when(it){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(books = it.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = it.message ?: "Unknown error occurred.")
                    }
                }
            }
        }
    }

    private fun saveBook(books: List<BookDto>){
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when(val result = useCase.addBookUseCase(books)){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _eventFlow.emit(ResultEvent.ShowSnackbar("Books added successfully."))
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _eventFlow.emit(ResultEvent.ShowError(result.message ?: "Unknown error occurred."))
                }
            }
        }
    }

    private fun removeBook(bookId: String){
        viewModelScope.launch {
            when(val result = useCase.removeBookUseCase(bookId)){
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _eventFlow.emit(ResultEvent.ShowSnackbar("Book removed successfully."))
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _eventFlow.emit(ResultEvent.ShowError(result.message ?: "Failed to remove book."))
                }
            }
        }
    }



}