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
import com.viwath.srulibrarymobile.domain.usecase.dashboard_usecase.DashboardUseCase
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.EntryUseCase
import com.viwath.srulibrarymobile.presentation.event.DashboardEntryEvent
import com.viwath.srulibrarymobile.presentation.state.DashboardState
import com.viwath.srulibrarymobile.presentation.state.StudentState
import com.viwath.srulibrarymobile.utils.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for the Dashboard screen.
 *
 * This ViewModel is responsible for:
 * - Fetching and managing the dashboard data.
 * - Handling user interactions related to dashboard entries (e.g., saving attendance, getting student details).
 * - Exposing the dashboard state and events to the UI.
 * - Retrieving user information from TokenManager
 *
 * @property useCase The use case for fetching the dashboard data.
 * @property entryUseCase The use case for handling dashboard entry operations (save attend, get student).
 * @property userPreferences The manager for handling user tokens and retrieving user data.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val useCase: DashboardUseCase,
    private val entryUseCase: EntryUseCase,
    userPreferences: UserPreferences
): ViewModel(){

    private val _state = MutableLiveData(DashboardState())
    val state: LiveData<DashboardState> = _state

    private val _eventChannel = Channel<StudentState>(Channel.BUFFERED)
    val eventFlow = _eventChannel.receiveAsFlow()

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    init {
        viewModelScope.launch {
            _username.value = userPreferences.getUsername() ?: "User"
        }
        if(_state.value?.dashboard == null){
            getDashboard()
        }
    }

    fun onEntryEvent(event: DashboardEntryEvent){
        when(event){
            is DashboardEntryEvent.SaveAttend -> saveEntry(event.studentId, event.purpose)
            is DashboardEntryEvent.GetStudent -> getStudentById(event.studentId.toLong())
        }
    }

    fun getDashboard() {
        useCase().onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = DashboardState(dashboard = result.data)
                }
                is Resource.Loading -> {
                    _state.value = DashboardState(isLoading = true)
                }
                is Resource.Error -> {
                    _state.value = DashboardState(error = result.message.toString())
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getStudentById(id: Long){
        viewModelScope.launch {
            _eventChannel.send(StudentState.GetStudentLoading)
            try {
                entryUseCase.getStudentByIDUseCase(id).collect{ result ->
                    when(result){
                        is Resource.Error -> {
                            _eventChannel.send(StudentState.GetStudentError(result.message.toString()))
                        }
                        is Resource.Loading -> {
                            _eventChannel.send(StudentState.GetStudentLoading)
                        }
                        is Resource.Success -> {
                            result.data?.let { student ->
                                _eventChannel.send(StudentState.GetStudentSuccess(student))
                            } ?: _eventChannel.send(StudentState.GetStudentError("Student not found"))
                        }

                    }
                }
            }catch (e: Exception){
                _eventChannel.send(StudentState.GetStudentError(e.message ?: "Unknown Error!"))
            }
        }
    }

    private fun saveEntry(studentId: String, purpose: String){
        viewModelScope.launch {
            _eventChannel.send(StudentState.SaveAttendLoading)
            try {
                entryUseCase.saveAttendUseCase(studentId, purpose)
                _eventChannel.send(StudentState.SaveAttendSuccess)
            }catch (e: Exception){
                _eventChannel.send(StudentState.SaveAttendError(e.message ?: "Unknown Error!"))
            }
        }
    }
}