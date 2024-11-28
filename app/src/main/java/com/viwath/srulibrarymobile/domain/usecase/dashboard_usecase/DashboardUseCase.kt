package com.viwath.srulibrarymobile.domain.usecase.dashboard_usecase

import com.viwath.srulibrarymobile.common.result.CoreResult
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DashboardUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<CoreResult<Dashboard>> = flow {
        runCatching {
            emit(CoreResult.Loading())
            val dashboard = repository.getDashboard()
            dashboard
        }.fold(
            onSuccess = {
               emit(CoreResult.Success(it))
            },
            onFailure = { e ->
                when (e) {
                    is HttpException -> {
                        println(e.printStackTrace())
                        emit(CoreResult.Error(e.localizedMessage ?: "AN error occurred."))
                    }

                    is IOException -> {
                        println(e.printStackTrace())
                        emit(CoreResult.Error(e.localizedMessage ?: "Couldn't reach server."))
                    }

                    else -> {
                        println(e.printStackTrace())
                        emit(CoreResult.Error(e.localizedMessage ?: "Ot dg error nv na."))
                    }
                }
            }
        )
    }
}