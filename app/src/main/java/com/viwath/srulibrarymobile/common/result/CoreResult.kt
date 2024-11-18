package com.viwath.srulibrarymobile.common.result

sealed class CoreResult<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : CoreResult<T>(data)
    class Error<T>(message: String, data: T? = null): CoreResult<T>(data, message)
    class Loading<T>(data: T? = null): CoreResult<T>(data)
}