package com.viwath.srulibrarymobile.domain.model

data class AttendDetail(
    val studentId: Long,
    val studentName: String,
    val gender: String,
    val major: String,
    val generation: Int,
    val entryTimes: String,
    var exitingTimes: String?,
    val purpose: String,
    var status: String
)
