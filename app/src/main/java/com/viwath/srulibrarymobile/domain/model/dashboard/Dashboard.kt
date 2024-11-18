package com.viwath.srulibrarymobile.domain.model.dashboard

import com.viwath.srulibrarymobile.domain.model.AttendDetail

data class Dashboard(
    val cardData: List<com.viwath.srulibrarymobile.domain.model.dashboard.CardData>,
    val totalMajorVisitor: List<com.viwath.srulibrarymobile.domain.model.dashboard.TotalMajorVisitor>,
    val weeklyVisitor: com.viwath.srulibrarymobile.domain.model.dashboard.WeeklyVisitor,
    val bookAvailable: List<com.viwath.srulibrarymobile.domain.model.dashboard.BookAvailable>,
    val customEntry: List<com.viwath.srulibrarymobile.domain.model.AttendDetail>,
)