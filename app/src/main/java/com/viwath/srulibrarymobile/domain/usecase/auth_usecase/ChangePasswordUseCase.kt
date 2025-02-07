/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.auth.ChangePasswordRequest
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Use case for changing a user's password.
 *
 * This class handles the business logic for initiating a password change request.
 * It interacts with the [AuthRepository] to perform the actual password change operation
 * and emits the result wrapped in a [Resource] sealed class.
 *
 * @property repository The [AuthRepository] instance used to interact with the authentication data source.
 */
class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
){
    operator fun invoke(email: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val changePasswordRequest = ChangePasswordRequest(email, password)
            val response = repository.changePassword(changePasswordRequest)
            if (response == false){
                return@flow emit(Resource.Error("Something went wrong."))
            }else{
                return@flow emit(Resource.Success(true))
            }
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