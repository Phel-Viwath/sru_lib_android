/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain

/**
 * `HandleDataError` is a utility object responsible for providing user-friendly error messages
 * based on different types of `DataError`. It categorizes errors into remote and local
 * types and returns specific messages for each error scenario.
 */
object HandleDataError {

    fun handleRemoteError(error: DataError.Remote): String {
        return when (error) {
            DataError.Remote.REQUEST_TIMEOUT -> "Request timed out, please try again."
            DataError.Remote.TOO_MANY_REQUESTS -> "Too many requests, slow down."
            DataError.Remote.NO_INTERNET -> "No internet connection, check your network."
            DataError.Remote.SERVER -> "Server error, try again later."
            DataError.Remote.SERIALIZATION -> "Data serialization error."
            DataError.Remote.UNKNOWN -> "An unknown error occurred."
        }
    }

    fun handleLocalError(error: DataError.Local): String{
        return when(error){
            DataError.Local.DISK_FULL -> "Disk is full, please try again later."
            DataError.Local.UNKNOWN -> "An unknown error occurred."
        }
    }

}