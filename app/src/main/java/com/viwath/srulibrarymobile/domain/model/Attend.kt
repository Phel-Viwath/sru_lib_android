/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model

data class Attend (
    val attendId: AttendId?,
    val studentId: StudentId,
    val entryTimes: EntryTimes,
    val exitingTimes: ExitingTimes?,
    val purpose: Purpose,
    val date: Date
)
