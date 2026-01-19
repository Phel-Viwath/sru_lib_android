/*
 * Copyright (c) 2026.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.domain.model.entry.Attend
import com.viwath.srulibrarymobile.domain.model.entry.Entry

interface AttendRepository {
    suspend fun newAttend(studentId: String, purpose: String): Attend
    suspend fun getStudentById(id: Long): Result<Students, DataAppError.Remote>
    suspend fun getRecentEntryData(): Entry
    suspend fun updateExitingTime(studentId: Long): Boolean
    suspend fun checkExitingAttend(id: String): String
}