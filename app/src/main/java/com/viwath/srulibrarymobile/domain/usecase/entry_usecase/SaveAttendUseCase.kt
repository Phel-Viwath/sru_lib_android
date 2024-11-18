package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import javax.inject.Inject

class SaveAttendUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    suspend operator fun invoke(studentId: String, purpose: String){
        if (purpose.isBlank() || studentId.isEmpty()) {
            throw Exception("Purpose cannot be null")
        }
        try {
            repository.newAttend(studentId, purpose)
        } catch (e: Exception) {
            println("Error saving attendance: ${e.message}") // Log the error
            throw e
        }
    }
}