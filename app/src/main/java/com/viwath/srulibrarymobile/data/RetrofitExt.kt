/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.data

import android.util.Log
import com.viwath.srulibrarymobile.domain.DataAppError
import com.viwath.srulibrarymobile.domain.Result
import kotlinx.coroutines.ensureActive
import retrofit2.Response
import java.net.SocketTimeoutException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.coroutineContext

/**
 * Executes a network call within a safe environment, handling common exceptions and transforming the response
 * into a `Result` object.
 *
 * This function is designed to be used with Retrofit's `Response` objects and provides a standardized way
 * to handle network requests, including error handling for timeouts, no internet connection, and other
 * unexpected exceptions.
 *
 * @param execute A lambda representing the network call to be executed. It should return a `Response<T>`.
 * @param T The type of data expected in the successful response body.
 * @return A `Result<T, DataError.Remote>` object, representing either a successful response or a
 *         specific remote error.
 *         - `Result.Success(T)`: If the network call is successful and the response body is valid.
 *         - `Result.Error(DataError.Remote)`: If any error occurs during the network call, such as:
 *           - `DataError.Remote.REQUEST_TIMEOUT`: If the network request times out (SocketTimeoutException).
 *           - `DataError.Remote.NO_INTERNET`: If there's no internet connection (UnresolvedAddressException or UnknownHostException).
 *           - `DataError.Remote.UNKNOWN`: If any other unexpected exception occurs.
 *           - other data error defined in `responseToResult`
 *
 * @throws CancellationException if the coroutine is canceled during the network operation. This is implicitly handled by `coroutineContext.ensureActive()`.
 *
 * @sample
 * ```
 * suspend fun getUser(userId: String): Result<User, DataError.Remote> {
 *     return safeCall { apiService.getUser(userId) }
 * }
 * ```
 */
suspend inline fun <reified T> safeCall(
    execute: () -> Response<T>
): Result<T, DataAppError.Remote>{
    val response = try {
        execute()
    }catch (e: SocketTimeoutException){
        Log.d("RetrofitExt", "safeCall: ${e.message}")
        return Result.Error(DataAppError.Remote.REQUEST_TIMEOUT)
    }catch (e: UnresolvedAddressException){
        Log.d("RetrofitExt", "safeCall: ${e.message}")
        return Result.Error(DataAppError.Remote.NO_INTERNET)
    }catch (e: Exception){
        Log.d("RetrofitExt", "safeCall: ${e.message}")
        coroutineContext.ensureActive()
        return Result.Error(DataAppError.Remote.UNKNOWN)
    }
    return responseToResult(response)
}

inline fun <reified T> responseToResult(
    response: Response<T>
): Result<T, DataAppError.Remote>{
    return when{
        response.isSuccessful -> {
            val body = response.body()
            if (body != null) {
                Result.Success(body)
            } else {
                Result.Error(DataAppError.Remote.SERIALIZATION)
            }
        }
        response.code() == 403 -> Result.Error(DataAppError.Remote.FORBIDDEN)
        response.code() == 408 -> Result.Error(DataAppError.Remote.REQUEST_TIMEOUT)
        response.code() == 429 -> Result.Error(DataAppError.Remote.TOO_MANY_REQUESTS)
        response.code() in 500..599 -> Result.Error(DataAppError.Remote.SERVER)
        else -> Result.Error(DataAppError.Remote.UNKNOWN)
    }
}