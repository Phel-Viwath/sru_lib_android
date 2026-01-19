/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.repository

import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard

/**
 * CoreRepository interface provides access to the core data operations.
 * This interface defines methods for interacting with various data sources,
 * including dashboards, students, attendance, books, borrows, and donations.
 */
interface DashboardRepository {
    suspend fun getDashboard(): Dashboard
}