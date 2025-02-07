/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import javax.inject.Inject

/**
 * `SaveAttendUseCase` is a use case responsible for saving a new attendance record for a student.
 *
 * This class encapsulates the logic for validating and saving attendance data to the repository.
 * It interacts with the [CoreRepository] to persist the attendance information.
 *
 * @property repository The [CoreRepository] instance used for interacting with the data layer.
 * @constructor Creates a new instance of `SaveAttendUseCase`.
 */
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