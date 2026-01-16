/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.book.Book
import com.viwath.srulibrarymobile.domain.model.borrow.BorrowRequest
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.BookUseCase
import com.viwath.srulibrarymobile.domain.usecase.borrow_usecase.BorrowUseCase
import com.viwath.srulibrarymobile.presentation.event.BookTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.book_state.BookTabState
import com.viwath.srulibrarymobile.presentation.state.book_state.UploadState
import com.viwath.srulibrarymobile.utils.collectResource
import com.viwath.srulibrarymobile.utils.updateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [BookTabViewModel] is a ViewModel class responsible for managing the UI-related data
 * and business logic for the book management screen. It interacts with use cases
 * to perform actions such as loading, saving, updating, removing, borrowing, and
 * searching for books.
 *
 * @property useCase The [BookUseCase] instance for interacting with book-related data.
 * @property borrowUseCase The [BorrowUseCase] instance for handling book borrowing operations.
 */
@HiltViewModel
class BookTabViewModel @Inject constructor(
    private val useCase: BookUseCase,
    private val borrowUseCase: BorrowUseCase
): ViewModel(){

    // store current book
    private val _booksList: MutableList<Book> = mutableListOf()
    private var _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> get() = _genres

    private val _state = MutableStateFlow(BookTabState())
    val state: StateFlow<BookTabState> get() = _state

    private val _eventFlow = MutableSharedFlow<ResultEvent>()
    val eventFlow: SharedFlow<ResultEvent> get() = _eventFlow.asSharedFlow()

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState = _uploadState.asStateFlow()

    private val _findStudentChannel = Channel<Boolean>()
    val findStudentChannel get() = _findStudentChannel.receiveAsFlow()

    private var isInitialLoad = true

    init {
        viewModelScope.launch { loadInitData() }
    }

    fun loadInitData() {
        viewModelScope.launch(Dispatchers.IO) {
            val bookDeferred = async{ loadListBook() }
            bookDeferred.await()
        }
    }

    fun onEvent(event: BookTabEvent){
        when(event){
            // button event
            is BookTabEvent.SaveBook -> { saveBook() }
            is BookTabEvent.Remove -> { removeBook() }
            is BookTabEvent.UpdateBook -> { updateBook() }
            is BookTabEvent.Borrow -> { borrowBook() }
            is BookTabEvent.UploadBook -> { uploadBook() }
            is BookTabEvent.GetStudent -> { getStudentById() }
            is BookTabEvent.FilterGenre -> {
                if (!isInitialLoad) {
                    filterByGenre()
                }
                isInitialLoad = false
            }
            is BookTabEvent.Search -> { searchBook() }
            is BookTabEvent.FilterByIdOrTitle -> { filterBookByIdOrTitle() }

            // edit text change
            is BookTabEvent.BookIdChange -> _state.updateState { copy(bookId = event.bookId) }
            is BookTabEvent.QuanChange -> _state.updateState { copy(bookQuan = event.quan) }
            is BookTabEvent.AuthorChange -> _state.updateState { copy(author = event.author ?: "") }
            is BookTabEvent.BookTittleChange ->_state.updateState { copy(bookTitle = event.bookTitle) }
            is BookTabEvent.PublicYearChange -> _state.updateState { copy(publicYear = event.publicYear) }
            is BookTabEvent.CollegeChange -> _state.updateState { copy(collegeId = event.collegeId) }
            is BookTabEvent.LanguageChange -> _state.updateState { copy(languageId = event.languageId) }
            is BookTabEvent.GenreChange -> _state.updateState { copy(genre = event.genre) }
            is BookTabEvent.FileChange -> _state.updateState { copy(file = event.file) }

            is BookTabEvent.StudentIdChange -> _state.updateState { copy(studentId = event.studentId) }
            is BookTabEvent.FilterGenreChange -> _state.updateState { copy(genreFilter = event.filter) }
            is BookTabEvent.SearchChange -> _state.updateState { copy(searchKeywordChange = event.search) }

        }
    }

    private fun filterBookByIdOrTitle() {
        val keyword = _state.value.searchKeywordChange
        val sortedList = _booksList.filter { it.bookId.contains(keyword) || it.bookTitle.contains(keyword) }
        _state.updateState { copy(books = sortedList) }
    }


    private suspend fun loadListBook(){
        useCase.getBooksUseCase().collectResource(
            onLoading = {_state.updateState { copy(isLoading = true) }},
            onSuccess = { book ->
                val listData = listOf("All") + book.getGenres()
                _genres.value = listData
                _booksList.clear()
                _booksList.addAll(book)
                _state.updateState { copy(isLoading = false, books = book) }
            },
            onError = {error -> _state.updateState { copy(isLoading = false, error = error) }}
        )
    }

    private fun getStudentById(){
        val studentId = _state.value.studentId
        if (studentId <= 0){
            emitEvent(ResultEvent.ShowError("Student ID fail."))
        }
        viewModelScope.launch{
            useCase.getStudentByIDUseCase(studentId).collectResource(
                onLoading = {_state.updateState { copy(isLoading = true) }},
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    viewModelScope.launch{
                        _findStudentChannel.send(false)
                    }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    viewModelScope.launch{
                        _findStudentChannel.send(true)
                    }
                    emitEvent(ResultEvent.ShowSuccess("Student founded."))
                }
            )
        }
    }

    private fun saveBook(){
        val (isValid, errorMessage) = validateBookData()
        if (!isValid) {
            emitEvent(ResultEvent.ShowError(errorMessage))
            return
        }
        val newBook = Book(
            bookId = _state.value.bookId,
            bookTitle = _state.value.bookTitle,
            bookQuan = _state.value.bookQuan,
            publicationYear = _state.value.publicYear,
            author = _state.value.author,
            languageId = _state.value.languageId,
            collegeId = _state.value.collegeId,
            genre = _state.value.genre,
            receiveDate = null
        )
        viewModelScope.launch {
            useCase.addBookUseCase(newBook).collectResource(
                onLoading = { _state.updateState { copy(isLoading = true) } },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Book added successfully."))
                },
                onError = { error ->
                    _state.updateState { copy(isLoading = false) }
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
            useCase.uploadBookUseCase(file).collect{state ->
                _uploadState.value = state
            }
        }
    }

    private fun removeBook(){
        val bookId = _state.value.bookId
        if (bookId.isEmpty())
            emitEvent(ResultEvent.ShowError("Id id empty."))
        viewModelScope.launch {
            useCase.removeBookUseCase(bookId).collectResource(
                onLoading = { _state.updateState { copy(isLoading = true) } },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Book removed successfully."))
                },
                onError = { error ->
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(error))
                }
            )
        }
    }

    private fun updateBook(){
        val (isValid, errorMessage) = validateBookData()
        if (!isValid){
            emitEvent(ResultEvent.ShowError(errorMessage))
            return
        }

        val newBook = Book(
            bookId = _state.value.bookId,
            bookTitle = _state.value.bookTitle,
            bookQuan = _state.value.bookQuan,
            publicationYear = _state.value.publicYear,
            author = _state.value.author,
            languageId = _state.value.languageId,
            collegeId = _state.value.collegeId,
            genre = _state.value.genre,
            receiveDate = null
        )

        viewModelScope.launch(Dispatchers.IO){
            useCase.updateBookUseCase(newBook).collectResource(
                onLoading = {
                    _state.updateState { copy(isLoading = true) }
                },
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Book updated successfully."))
                }
            )
        }
    }

    private fun borrowBook(){
        val studentId = _state.value.studentId
        val bookId = _state.value.bookId
        val bookQuan = _state.value.bookQuan
        if (studentId <= 0 || bookQuan <= 0 || bookId.isEmpty()){
            emitEvent(ResultEvent.ShowError("Invalid value!"))
        }
        val borrowRequest = BorrowRequest(bookId, studentId, bookQuan)
        viewModelScope.launch{
            borrowUseCase.borrowBookUseCase(borrowRequest).collectResource(
                onLoading = { _state.updateState { copy(isLoading = true) } },
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Book borrowed successfully."))
                }
            )
        }
    }

    private fun filterByGenre(){
        val genre = _state.value.genreFilter
        if (genre == "All"){
            viewModelScope.launch{
                loadListBook()
            }
        }
        else{
            val filteredBook = _booksList.filter { it.genre == genre }
            _state.updateState { copy(isLoading = false, books = filteredBook) }
        }
    }

    private fun searchBook(){
        val keyword = _state.value.searchKeywordChange
        viewModelScope.launch{
            useCase.searchBookUseCase(keyword).collectResource(
                onLoading = { _state.updateState { copy(isLoading = true) } },
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false, books = it) }
                }
            )
        }
    }

    private fun List<Book>.getGenres(): List<Genre> = this.map { it.genre }.distinct()

    private fun validateBookData(): Pair<Boolean, String>{
        val bookId = _state.value.bookId
        val bookTitle = _state.value.bookTitle
        val bookQuan = _state.value.bookQuan
        val genre = _state.value.genre
        val college = _state.value.collegeId

        return when {
            bookId.isEmpty() -> false to "Book ID is required."
            bookTitle.isEmpty() -> false to "Book title is required."
            college.isEmpty() -> false to "College is required."
            bookQuan <= 0 -> false to "Quantity must be greater than 0."
            genre.isEmpty() -> false to "Genre is required."
            else -> true to ""
        }
    }

    private fun emitEvent(event: ResultEvent){
        viewModelScope.launch{_eventFlow.emit(event)}
    }

}