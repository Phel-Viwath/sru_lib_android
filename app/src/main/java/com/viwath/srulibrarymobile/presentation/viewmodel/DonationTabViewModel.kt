/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.domain.model.Donation
import com.viwath.srulibrarymobile.domain.model.DonationIO
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.usecase.donation_usecase.DonationUseCase
import com.viwath.srulibrarymobile.presentation.event.DonationTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.book_state.DonationState
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
class DonationTabViewModel @Inject constructor(
    private val useCase: DonationUseCase
): ViewModel(){

    private val _donationList: MutableList<Donation> = mutableListOf()

    private val _genreList = MutableStateFlow<List<Genre>>(emptyList())
    val genreList: StateFlow<List<Genre>> get() = _genreList

    private val _state = MutableStateFlow(DonationState())
    val state: StateFlow<DonationState> get() = _state

    private val _resultEvent = MutableSharedFlow<ResultEvent>()
    val resultEvent: SharedFlow<ResultEvent> get() = _resultEvent

    private var isInitialLoad = true

    init {
        viewModelScope.launch{
            loadInitData()
        }
    }

    fun onEvent(event: DonationTabEvent){
        when(event){
            is DonationTabEvent.OnSubmit -> save()
            is DonationTabEvent.OnUpdate -> update()
            is DonationTabEvent.OnFilter -> {
                if (!isInitialLoad)
                    filterDonationList()
                isInitialLoad = false
            }
            is DonationTabEvent.OnSearch -> {}
            is DonationTabEvent.OnReloadList -> {
                viewModelScope.launch {
                    loadInitData()
                }
            }

            is DonationTabEvent.OnFilterGenreChange ->
                _state.updateState { copy(filterGenreChange = event.filter) }
            is DonationTabEvent.OnSearchChange ->
                _state.updateState { copy(searchChange = event.search) }

            is DonationTabEvent.OnDonatorIdChange ->
                _state.updateState { copy(donatorId = event.donatorId) }
            is DonationTabEvent.OnDonatorNameChange ->
                _state.updateState { copy(donatorName = event.donatorName) }
            is DonationTabEvent.OnBookIdChange ->
                _state.updateState { copy(bookId = event.bookId) }
            is DonationTabEvent.OnBookTitleChange ->
                _state.updateState { copy(bookTitle = event.bookTitle) }
            is DonationTabEvent.OnBookQuantityChange ->
                _state.updateState { copy(bookQuan = event.bookQuan) }
            is DonationTabEvent.OnLanguageIdChange ->
                _state.updateState { copy(languageId = event.languageId) }
            is DonationTabEvent.OnCollegeIdChange ->
                _state.updateState { copy(collegeId = event.collegeId) }
            is DonationTabEvent.OnAuthorChange ->
                _state.updateState { copy(author = event.author) }
            is DonationTabEvent.OnPublicationYearChange ->
                _state.updateState { copy(publicationYear = event.publicationYear) }
            is DonationTabEvent.OnGenreChange ->
                _state.updateState { copy(genre = event.genre) }
            is DonationTabEvent.OnDonateDateChange ->
                _state.updateState { copy(donateDate = event.donateDate) }
        }
    }

    private suspend fun loadInitData() = coroutineScope{
        val donationDeferred = async{ loadDonationList() }
        donationDeferred.await()
    }

    private fun emitEvent(event: ResultEvent){
        viewModelScope.launch {
            _resultEvent.emit(event)
        }
    }

    private fun loadDonationList(){
        viewModelScope.launch{
            useCase.getAllDonationUseCase().collectResource(
                onLoading = {
                    _state.updateState { copy(isLoading = true) }
                },
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {donation ->
                    val listGenre = listOf("All") + donation.getGenres()
                    _genreList.value = listGenre
                    _donationList.clear()
                    _donationList.addAll(donation)
                    _state.updateState { copy(isLoading = false, donationList = donation) }
                }
            )
        }
    }

    private fun filterDonationList(){
        val genre = _state.value.filterGenreChange
        if (genre == "All")
            viewModelScope.launch{
                loadDonationList()
            }
        else{
            val filterList = _donationList.filter { it.genre == genre }
            _state.updateState { copy(donationList = filterList, isLoading = false) }
        }
    }

    private fun save(){
        val donatorName = _state.value.donatorName
        val bookId = _state.value.bookId
        val bookTitle = _state.value.bookTitle
        val bookQuan = _state.value.bookQuan
        val languageId = _state.value.languageId
        val collegeId = _state.value.collegeId
        val author = _state.value.author
        val publicationYear = _state.value.publicationYear
        val genre = _state.value.genre
        val donateDate = _state.value.donateDate
        if (donatorName.isEmpty() || bookId.isEmpty() || bookTitle.isEmpty() || bookQuan == 0 || genre.isEmpty() || donateDate.isEmpty()){
            emitEvent(ResultEvent.ShowError("Please fill all the fields"))
            return
        }
        viewModelScope.launch{
            val donation = DonationIO(
                donatorId = null,
                donatorName = donatorName,
                bookId = bookId,
                bookTitle = bookTitle,
                bookQuan = bookQuan,
                languageId = languageId,
                collegeId = collegeId,
                author = author,
                publicationYear = publicationYear,
                genre = genre,
                donateDate = donateDate
            )
            useCase.addDonationUseCase(donation).collectResource(
                onLoading = {_state.updateState { copy(isLoading = true) }},
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Donation added successfully"))
                }
            )
        }
    }

    private fun update(){
        val donatorId = _state.value.donatorId
        val donatorName = _state.value.donatorName
        val bookId = _state.value.bookId
        val bookTitle = _state.value.bookTitle
        val bookQuan = _state.value.bookQuan
        val languageId = _state.value.languageId
        val collegeId = _state.value.collegeId
        val author = _state.value.author
        val publicationYear = _state.value.publicationYear
        val genre = _state.value.genre
        val donateDate = _state.value.donateDate
        if (donatorName.isEmpty() || bookId.isEmpty() || bookTitle.isEmpty() || bookQuan == 0 || genre.isEmpty() || donateDate.isEmpty()){
            emitEvent(ResultEvent.ShowError("Please fill all the fields"))
            return
        }

        viewModelScope.launch{
            val donation = DonationIO(
                donatorId = donatorId,
                donatorName = donatorName,
                bookId = bookId,
                bookTitle = bookTitle,
                bookQuan = bookQuan,
                languageId = languageId,
                collegeId = collegeId,
                author = author,
                publicationYear = publicationYear,
                genre = genre,
                donateDate = donateDate
            )
            useCase.updateDonationUseCase(donation).collectResource(
                onLoading = {_state.updateState { copy(isLoading = true) }},
                onError = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowError(it))
                },
                onSuccess = {
                    _state.updateState { copy(isLoading = false) }
                    emitEvent(ResultEvent.ShowSuccess("Donation update successful."))
                }
            )
        }
    }

    private fun List<Donation>.getGenres(): List<Genre> = this.map { it.genre }.distinct()


}