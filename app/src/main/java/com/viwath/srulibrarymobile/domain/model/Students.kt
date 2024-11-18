package com.viwath.srulibrarymobile.domain.model

data class Students(
    val studentId: Long?,
    val studentName: String,
    val gender: String,
    val dateOfBirth: String,
    val degreeLevel: String,
    val majorName: String,
    val generation: Int
)