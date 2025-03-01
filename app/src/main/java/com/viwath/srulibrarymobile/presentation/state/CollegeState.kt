/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state

import com.viwath.srulibrarymobile.domain.model.College

data class CollegeState(
    val isLoading: Boolean = false,
    val colleges: List<College> = emptyList(),
    val error: String = ""
)
