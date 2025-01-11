/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

data class EntryUseCase(
    val getStudentByIDUseCase: GetStudentByIDUseCase,
    val saveAttendUseCase: SaveAttendUseCase,
    val recentEntryUseCase: GetRecentEntryUseCase,
    val updateExitingUseCase: UpdateExitingUseCase,
    val checkExitingUseCase: CheckExitingUseCase
)
