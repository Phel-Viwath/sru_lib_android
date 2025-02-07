/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.dashboard_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * [DashboardUseCase] is a Use Case class responsible for fetching and handling the Dashboard data.
 *
 * It interacts with the [CoreRepository] to retrieve the [Dashboard] information.
 * It handles potential errors during the data retrieval process, such as network or server issues,
 * and emits the result as a [Flow] of [Resource] objects.
 *
 * @property repository The [CoreRepository] instance used to access the data layer.
 */
class DashboardUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<Resource<Dashboard>> = flow {
        runCatching {
            emit(Resource.Loading())
            val dashboard = repository.getDashboard()
            dashboard
        }.fold(
            onSuccess = {
               emit(Resource.Success(it))
            },
            onFailure = { e ->
                when (e) {
                    is HttpException -> {
                        println(e.printStackTrace())
                        emit(Resource.Error(e.localizedMessage ?: "AN error occurred."))
                    }

                    is IOException -> {
                        println(e.printStackTrace())
                        emit(Resource.Error(e.localizedMessage ?: "Couldn't reach server."))
                    }

                    else -> {
                        println(e.printStackTrace())
                        emit(Resource.Error(e.localizedMessage ?: "Ot dg error nv na."))
                    }
                }
            }
        )
    }
}