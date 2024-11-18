package com.viwath.srulibrarymobile.domain.model

import java.time.LocalTime
import java.time.LocalDate

data class Attend (
    val attendId: Long?,
    val studentId: Long,
    val entryTimes: String,
    val exitingTimes: String?,
    val purpose: String,
    val date: String
)
