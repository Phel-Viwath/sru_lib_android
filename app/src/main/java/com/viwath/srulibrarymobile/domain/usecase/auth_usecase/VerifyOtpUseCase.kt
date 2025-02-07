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
 * Use case for verifying an OTP (One-Time Password).
 *
 * This class encapsulates the logic for verifying an OTP against an authentication repository.
 * It handles various scenarios, including successful verification, network errors, HTTP exceptions,
 * and general exceptions.
 *
 * @property repository The [AuthRepository] used to interact with the authentication service.
 * @constructor Creates a [VerifyOtpUseCase] instance with the provided [AuthRepository].
 */
class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(otp: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.verifyOtp(otp)
            emit(Resource.Success(response))
        }catch (e: IOException){
            Log.e("VerifyOtpUseCase", "invoke: ${e.message}", e.cause)
            emit(Resource.Error(e.message.toString()))
        }catch (e: HttpException){
            Log.e("VerifyOtpUseCase", "invoke: ${e.message}", e.cause)
            emit(Resource.Error(e.message()))
        }catch (e: Exception){
            Log.e("VerifyOtpUseCase", "invoke: ${e.message}", e.cause)
            emit(Resource.Error(e.message.toString()))
        }
    }
}