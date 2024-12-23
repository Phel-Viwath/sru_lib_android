package com.viwath.srulibrarymobile.presentation.event

sealed class ResultEvent {
    data class ShowSnackbar(val message: String) : ResultEvent()
    data class ShowError(val errorMsg: String) : ResultEvent()
}