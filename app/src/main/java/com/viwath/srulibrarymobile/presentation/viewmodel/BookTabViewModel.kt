package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.viwath.srulibrarymobile.domain.usecase.book_usecase.BookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookTabViewModel @Inject constructor(
    private val useCase: BookUseCase
): ViewModel(){

}