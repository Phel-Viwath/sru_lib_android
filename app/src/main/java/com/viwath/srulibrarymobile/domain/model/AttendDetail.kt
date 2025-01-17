/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model

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
