package com.viwath.srulibrarymobile.presentation.state

import com.viwath.srulibrarymobile.domain.model.Students

sealed class StudentState {
    data class GetStudentSuccess(val students: Students): StudentState()
    data class GetStudentError(val errorMsg: String): StudentState()
    data object GetStudentLoading: StudentState()
    data object SaveAttendSuccess : StudentState()
    data class SaveAttendError(val errorMsg: String) : StudentState()
    data object SaveAttendLoading : StudentState()
}