/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

/**
 * A use case responsible for requesting a One-Time Password (OTP) for a given email.
 *
 * This class interacts with the [AuthRepository] to send a request for an OTP to the server.
 * It handles potential network and server errors, emitting appropriate [Resource] states.
 *
 * @property repository The [AuthRepository] instance used to communicate with the authentication data source.
 */
class RequestOtpUseCase @Inject constructor(
    private val repository: AuthRepository
){
    operator fun invoke(email: String): Flow<Resource<Boolean>> = flow{
        emit(Resource.Loading())
        try {
            val response = repository.requestOtp(email)
            emit(Resource.Success(response))
        }catch (e: IOException){
            Log.e("VerifyOtpUseCase", "invoke: ${e.message}", e.cause)
            emit(Resource.Error("${e.message}"))
        }catch (e: HttpException){
            Log.e("VerifyOtpUseCase", "invoke: ${e.message}", e.cause)
            emit(Resource.Error("${e.message}"))
        }catch (e: Exception){
            Log.e("VerifyOtpUseCase", "invoke: ${e.message}", e.cause)
            emit(Resource.Error("${e.message}"))
        }
    }
}