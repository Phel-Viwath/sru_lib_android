/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

/**
 * A use case class that encapsulates various student attendance-related operations.
 * This class aggregates multiple use cases to handle student entry and exit management.
 *
 * @property getStudentByIDUseCase Use case for retrieving student information by their ID
 * @property saveAttendUseCase Use case for saving new attendance records
 * @property getRecentEntryUseCase Use case for retrieving recent entry records
 * @property updateExitingUseCase Use case for updating exit records
 * @property checkExitingUseCase Use case for verifying if a student has exited
 */
data class EntryUseCase(
    val getStudentByIDUseCase: GetStudentByIDUseCase,
    val saveAttendUseCase: SaveAttendUseCase,
    val getRecentEntryUseCase: GetRecentEntryUseCase,
    val updateExitingUseCase: UpdateExitingUseCase,
    val checkExitingUseCase: CheckExitingUseCase
)
