/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain

/**
 * Represents the result of an operation that can either succeed with a value of type [D] or fail with an error of type [E].
 *
 * This is a sealed interface, meaning all its possible implementations are known at compile time.
 * It's useful for modeling operations that can have two distinct outcomes, success or failure,
 * and handling them explicitly without relying on exceptions.
 *
 * @param D The type of the data that is returned upon successful completion.
 * @param E The type of the error that occurred upon failure. It must be a subtype of [Error].
 */
sealed interface Result<out D,out E: Error>{
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E: com.viwath.srulibrarymobile.domain.Error>(val error: E) : Result<Nothing, E>
}

inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E>{
    return when(this){
        is Result.Success -> Result.Success(map(data))
        is Result.Error -> Result.Error(error)
    }
}

//fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E>{
//    return map {  }
//}
//
//inline fun <T, E: Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E>{
//    return when(this){
//        is Result.Success -> {
//            action(data)
//            this
//        }
//        is Result.Error -> this
//    }
//}
//
//inline fun <T, E: Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
//    return when (this) {
//        is Result.Error -> {
//            action(error)
//            this
//        }
//        is Result.Success -> this
//    }
//}

