package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viwath.srulibrarymobile.common.result.CoreResult
import com.viwath.srulibrarymobile.domain.usecase.entry_usecase.EntryUseCase
import com.viwath.srulibrarymobile.presentation.event.QrEntryEvent
import com.viwath.srulibrarymobile.presentation.state.QrFragmentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrFragmentViewModel @Inject constructor(
    private val useCase: EntryUseCase
): ViewModel(){
    private val _state = MutableStateFlow<QrFragmentState>(QrFragmentState.Empty)
    val state: StateFlow<QrFragmentState> get() = _state

    init {
        getRecentEntryData()
    }

    fun onEvent(event: QrEntryEvent){
        when(event){
            is QrEntryEvent.LoadStudent -> checkExitingTime(event.studentId.toString())
            is QrEntryEvent.SaveAttention -> saveAttend(event.studentId, event.purpose)
            is QrEntryEvent.LoadRecentEntry -> getRecentEntryData()
        }
    }

    private fun loadStudent(id: Long){
        viewModelScope.launch {
            useCase.getStudentByIDUseCase(id).collect{ result ->
                when(result){
                    is CoreResult.Loading -> _state.value = QrFragmentState.Loading
                    is CoreResult.Error -> _state.value = QrFragmentState.Error(result.message ?: "Unknown error.")
                    is CoreResult.Success -> {
                        result.data?.let {
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
        useCase.recentEntryUseCase().onEach { result ->
            when(result){
                is CoreResult.Loading -> {
                    _state.value = QrFragmentState.Loading
                }
                is CoreResult.Success -> {
                    _state.value = QrFragmentState.EntryState(result.data)
                }
                is CoreResult.Error -> {
                    _state.value = QrFragmentState.Error(result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun checkExitingTime(studentId: String){
        viewModelScope.launch {
            useCase.checkExitingUseCase(studentId).collect{
                when(it){
                    is CoreResult.Loading -> _state.value = QrFragmentState.Loading
                    is CoreResult.Error -> _state.value = QrFragmentState.Error(it.message ?: "Unknown error.")
                    is CoreResult.Success -> {
                        if (it.data == "new attend!" || it.data == "exited"){
                            loadStudent(studentId.toLong())
                        }
                        else{
                            useCase.updateExitingUseCase(studentId.toLong()).collect{ updateResult ->
                                when(updateResult){
                                    is CoreResult.Loading -> _state.value = QrFragmentState.Loading
                                    is CoreResult.Error -> _state.value = QrFragmentState.Error(updateResult.message)
                                    is CoreResult.Success -> loadStudent(studentId.toLong())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}