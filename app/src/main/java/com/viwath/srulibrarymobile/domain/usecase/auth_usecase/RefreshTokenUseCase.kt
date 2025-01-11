/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.auth_usecase

import android.util.Log
import com.viwath.srulibrarymobile.domain.repository.AuthRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import okio.IOException
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return try{
            withTimeout(5000L){
                repository.refreshToken()
            }
        }catch (e: TimeoutCancellationException) {
            Log.e("RefreshTokenUseCase", "Timeout: ${e.message}", e.cause)
            false
        } catch (e: IOException) {
            Log.e("RefreshTokenUseCase", "invoke: ${e.message}", e.cause)
            false
        }
    }
}