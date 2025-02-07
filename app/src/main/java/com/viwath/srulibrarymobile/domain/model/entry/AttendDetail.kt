/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.entry

import com.viwath.srulibrarymobile.domain.model.EntryTimes
import com.viwath.srulibrarymobile.domain.model.ExitingTimes
import com.viwath.srulibrarymobile.domain.model.Gender
import com.viwath.srulibrarymobile.domain.model.Generation
import com.viwath.srulibrarymobile.domain.model.Major
import com.viwath.srulibrarymobile.domain.model.Purpose
import com.viwath.srulibrarymobile.domain.model.Status
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.model.StudentName

data class AttendDetail(
    val studentId: StudentId,
    val studentName: StudentName,
    val gender: Gender,
    val major: Major,
    val generation: Generation,
    val entryTimes: EntryTimes,
    var exitingTimes: ExitingTimes?,
    val purpose: Purpose,
    var status: Status
)
