/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.dashboard

import com.viwath.srulibrarymobile.domain.model.AttendDetail

data class Dashboard(
    val cardData: List<CardData>,
    val totalMajorVisitor: List<TotalMajorVisitor>,
    val weeklyVisitor: WeeklyVisitor,
    val bookAvailable: List<BookAvailable>,
    val customEntry: List<AttendDetail>
)