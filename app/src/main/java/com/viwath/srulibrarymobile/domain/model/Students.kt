/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model

data class Students(
    val studentId: StudentId?,
    val studentName: StudentName,
    val gender: Gender,
    val dateOfBirth: DateOfBirth,
    val degreeLevel: DegreeLevel,
    val majorName: MajorName,
    val generation: Generation
)