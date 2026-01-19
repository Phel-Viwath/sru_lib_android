/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data.repository

import android.util.Log
import com.viwath.srulibrarymobile.common.exception.CoreException
import com.viwath.srulibrarymobile.data.api.CoreApi
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.repository.DashboardRepository
import javax.inject.Inject

/**
 * Implementation of the [DashboardRepository] interface.
 * This class handles data operations related to core functionalities,
 * such as fetching dashboard information, managing student data,
 * handling attendances, book management, and donation features.
 *
 * @property api The [CoreApi] instance used for making network requests.
 */
class DashboardRepositoryImp @Inject constructor(
    private val api: CoreApi
) : DashboardRepository {
    override suspend fun getDashboard(): Dashboard {
        val response = api.dashboard()
        Log.d("Dashboard", "Response body: $response")
        return if (response.isSuccessful){
            response.body()?.let {
                Dashboard(
                    it.cardData,
                    it.totalMajorVisitor,
                    it.weeklyVisitor,
                    it.bookAvailable,
                    it.customEntry
                )
            } ?: throw CoreException("Response body is null")
        }
        else throw CoreException("Error: Dashboard")
    }
}