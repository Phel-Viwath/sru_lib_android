/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.BookUseCase
import com.viwath.srulibrarymobile.presentation.event.BookTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.book_state.BookTabState
import com.viwath.srulibrarymobile.presentation.state.book_state.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
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

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState = _uploadState.asStateFlow()

    init {
        viewModelScope.launch { loadInitData() }
    }

    private suspend fun loadInitData() = coroutineScope{
            val bookDeferred = async{loadListBook()}
            val languageDeferred = async{ loadLanguage() }
            val collegeDeferred = async{ loadCollege() }
            bookDeferred.await()
            languageDeferred.await()
            collegeDeferred.await()
    }

    fun onEvent(event: BookTabEvent){
        when(event){
            is BookTabEvent.SaveBook -> { saveBook() }
            is BookTabEvent.Remove -> { removeBook() }
            is BookTabEvent.UpdateBook -> { }
            is BookTabEvent.Borrow -> { }
            is BookTabEvent.UploadBook -> { uploadBook() }
            //
            is BookTabEvent.BookIdChange -> updateState { copy(bookId = event.bookId) }
            is BookTabEvent.QuanChange -> updateState { copy(bookQuan = event.quan) }
            is BookTabEvent.AuthorChange -> updateState { copy(author = event.author ?: "") }
            is BookTabEvent.BookTittleChange -> updateState { copy(bookTitle = event.bookTitle) }
            is BookTabEvent.PublicYearChange -> updateState { copy(publicYear = event.publicYear) }
            is BookTabEvent.CollegeChange -> updateState { copy(collegeId = event.collegeId) }
            is BookTabEvent.LanguageChange -> updateState { copy(languageId = event.languageId) }
            is BookTabEvent.GenreChange -> updateState { copy(genre = event.genre) }
            is BookTabEvent.FileChange -> updateState { copy(file = event.file) }

        }
    }

    private fun updateState(update: BookTabState.() -> BookTabState) {
        _state.value = _state.value.update()
    }

    private suspend fun loadListBook(){
        useCase.getBooksUseCase().collectResource(
            onLoading = {updateState { copy(isLoading = true) }},
            onSuccess = {book -> updateState { copy(isLoading = false, books = book) }},
            onError = {error -> updateState { copy(isLoading = false, error = error) }}
        )
    }

    private suspend fun loadLanguage(){
        useCase.getLanguageUseCase().collectResource(
            onLoading = {updateState { copy(isLoading = true) }},
            onSuccess = {language -> updateState { copy(isLoading = false, language = language) }},
            onError = {error -> updateState { copy(isLoading = false, error = error) }}
        )
    }

    private suspend fun loadCollege(){
        useCase.getCollegeUseCase().collectResource(
            onLoading = {updateState { copy(isLoading = true) }},
            onSuccess = {college -> updateState { copy(isLoading = false, college = college) }},
            onError = {error -> updateState { copy(isLoading = false, error = error) }}
        )
    }

    private fun saveBook(){
        val bookId = _state.value.bookId
        val bookTitle = _state.value.bookTitle
        val bookQuan = _state.value.bookQuan
        val publicYear = _state.value.publicYear
        val author = _state.value.author
        val language = _state.value.languageId
        val college = _state.value.collegeId
        val genre = _state.value.genre

        if (bookId.isEmpty() || bookTitle.isEmpty() || college.isEmpty() || bookQuan <= 0 || genre.isEmpty()){
            emitEvent(ResultEvent.ShowError("Please fill in all required fields."))
            return
        }
        val newBook = BookDto(
            bookId = bookId,
            bookTitle = bookTitle,
            bookQuan = bookQuan,
            publicationYear = publicYear,
            author = author,
            languageId = language,
            collegeId = college,
            genre = genre,
            receiveDate = null
        )
        viewModelScope.launch {
            useCase.addBookUseCase(newBook).collectResource(
                onLoading = { updateState { copy(isLoading = true) } },
                onSuccess = {
                    updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Book added successfully."))
                },
                onError = { error ->
                    updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(error))
                }
            )
        }

    }

    private fun uploadBook(){
        val file = _state.value.file
        if (file == null){
            emitEvent(ResultEvent.ShowError("File is missing."))
            return
        }
        viewModelScope.launch{
            useCase.uploadBookUseCase(file)
                .collect{state ->
                    _uploadState.value = state
                }
        }
    }

    private fun removeBook(){
        viewModelScope.launch {
            useCase.removeBookUseCase("").collectResource(
                onLoading = { updateState { copy(isLoading = true) } },
                onSuccess = {
                    updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Book removed successfully."))
                },
                onError = { error ->
                    updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(error))
                }
            )
        }
    }

    private  fun emitEvent(event: ResultEvent){
        viewModelScope.launch{_eventFlow.emit(event)}
    }
    private suspend inline fun <T> Flow<Resource<T>>.collectResource(
        crossinline onLoading: () -> Unit,
        crossinline onSuccess: (T) -> Unit,
        crossinline onError: (String) -> Unit
    ){
        collect { resource ->
            when (resource){
                is Resource.Loading -> onLoading()
                is Resource.Success -> onSuccess(resource.data ?: return@collect)
                is Resource.Error -> onError(resource.message ?: "Unknown error")
            }
        }
    }


}