/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.entry

import com.viwath.srulibrarymobile.domain.model.AttendId
import com.viwath.srulibrarymobile.domain.model.Date
import com.viwath.srulibrarymobile.domain.model.EntryTimes
import com.viwath.srulibrarymobile.domain.model.ExitingTimes
import com.viwath.srulibrarymobile.domain.model.Purpose
import com.viwath.srulibrarymobile.domain.model.StudentId

data class Attend (
    val attendId: AttendId?,
    val studentId: StudentId,
    val entryTimes: EntryTimes,
    val exitingTimes: ExitingTimes?,
    val purpose: Purpose,
    val date: Date
)
