package com.viwath.srulibrarymobile.domain.model.entry

import com.viwath.srulibrarymobile.domain.model.AttendDetail

data class Entry(
    val cardEntry: List<com.viwath.srulibrarymobile.domain.model.entry.CardEntry>,
    val attendDetail: List<com.viwath.srulibrarymobile.domain.model.AttendDetail>
)
