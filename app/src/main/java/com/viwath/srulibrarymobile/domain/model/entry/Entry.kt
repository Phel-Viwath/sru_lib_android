/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model.entry

import com.viwath.srulibrarymobile.domain.model.AttendDetail

data class Entry(
    val cardEntry: List<CardEntry>,
    val attendDetail: List<AttendDetail>
)
