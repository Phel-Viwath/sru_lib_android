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
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.EntryUseCase
import com.viwath.srulibrarymobile.presentation.event.QrEntryEvent
import com.viwath.srulibrarymobile.presentation.state.QrFragmentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the QR code scanning fragment.
 *
 * This ViewModel handles the business logic for the QR code scanning functionality,
 * including loading student data, saving attendance, retrieving recent entries,
 * and checking student exit/entry status.
 *
 * @property useCase The [EntryUseCase] responsible for handling data operations.
 */
@HiltViewModel
class QrFragmentViewModel @Inject constructor(
    private val useCase: EntryUseCase
): ViewModel(){
    private val _state = MutableStateFlow<QrFragmentState>(QrFragmentState.Idle)
    val state: StateFlow<QrFragmentState> get() = _state

    init {
        getRecentEntryData()
    }

    fun onEvent(event: QrEntryEvent){
        when(event){
            is QrEntryEvent.LoadStudent -> checkExitingTime(event.studentId)
            is QrEntryEvent.SaveAttention -> saveAttend(event.studentId, event.purpose)
            is QrEntryEvent.LoadRecentEntry -> getRecentEntryData()
        }
    }

    private fun loadStudent(id: Long){
        viewModelScope.launch {
            useCase.getStudentByIDUseCase(id).collect{ result ->
                when(result){
                    is Resource.Loading -> _state.value = QrFragmentState.Loading
                    is Resource.Error -> _state.value = QrFragmentState.Error(result.message ?: "Unknown error.")
                    is Resource.Success -> {
                        result.data?.let {
                            Log.d("QrFragmentViewModel", "loadStudent: $it")
                            _state.value = QrFragmentState.StudentLoaded(it)
                        } ?: run {
                            _state.value = QrFragmentState.Error("Student data is null")
                        }
                    }
                }
            }
        }
    }

    private fun saveAttend(studentId: String, purpose: String){
        viewModelScope.launch {
           try {
               _state.value = QrFragmentState.Loading
               useCase.saveAttendUseCase(studentId, purpose)
               _state.value = QrFragmentState.AttentionSaved()
           }catch (e: Exception){
               _state.value = QrFragmentState.Error(e.localizedMessage ?: "Unknown Error")
           }
        }
    }

    private fun getRecentEntryData(){
        viewModelScope.launch{
            useCase.recentEntryUseCase().collect { result ->
                when(result){
                    is Resource.Loading -> {
                        _state.value = QrFragmentState.Loading
                    }
                    is Resource.Success -> {
                        _state.value = QrFragmentState.EntryState(result.data)
                    }
                    is Resource.Error -> {
                        _state.value = QrFragmentState.Error(result.message)
                    }
                }
            }
        }
    }

    private fun checkExitingTime(studentId: String){
        viewModelScope.launch {
            useCase.checkExitingUseCase(studentId).collect{
                when(it){
                    is Resource.Loading -> _state.value = QrFragmentState.Loading
                    is Resource.Error -> _state.value = QrFragmentState.Error(it.message ?: "Unknown error.")
                    is Resource.Success -> {
                        val isNewAttend = it.data == "new attend!" || it.data == "exited"
                        Log.d("QrFragmentViewModel", "checkExitingTime: $isNewAttend")
                        if (isNewAttend){
                            loadStudent(studentId.toLong())
                        }
                        else{
                            useCase.updateExitingUseCase(studentId.toLong()).collect{ updateResult ->
                                when(updateResult){
                                    is Resource.Loading -> _state.value = QrFragmentState.Loading
                                    is Resource.Error -> _state.value = QrFragmentState.Error(updateResult.message)
                                    is Resource.Success -> loadStudent(studentId.toLong())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}