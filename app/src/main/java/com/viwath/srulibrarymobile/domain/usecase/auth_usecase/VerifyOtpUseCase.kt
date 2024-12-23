package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

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
            emit(Resource.Error(""))
        }catch (e: HttpException){
            Log.e("VerifyOtpUseCase", "invoke: ${e.message}", e.cause)
            emit(Resource.Error(""))
        }catch (e: Exception){
            Log.e("VerifyOtpUseCase", "invoke: ${e.message}", e.cause)
            emit(Resource.Error(""))
        }
    }


}