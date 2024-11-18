package com.viwath.srulibrarymobile.presentation.state

import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard

data class DashboardState(
    val isLoading: Boolean = false,
    val dashboard: Dashboard? = null,
    val error: String = ""
)
