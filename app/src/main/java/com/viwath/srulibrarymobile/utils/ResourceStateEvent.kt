/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import androidx.lifecycle.MutableLiveData
import com.viwath.srulibrarymobile.common.result.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Collects a [Flow] of [Resource] and handles different states (Loading, Success, Error).
 *
 * This inline function simplifies the process of collecting a flow that emits [Resource] objects.
 * It allows you to define specific actions to be performed for each state of the resource:
 * - **Loading:**  An action to perform when the resource is in the loading state.
 * - **Success:** An action to perform when the resource is in the success state, providing the data.
 * - **Error:** An action to perform when the resource is in the error state, providing an error message.
 *
 * @param T The type of data wrapped by the [Resource].
 * @param onLoading A lambda function to be executed when the [Resource] is in the [Resource.Loading] state.
 * @param onSuccess A lambda function to be executed when the [Resource] is in the [Resource.Success] state.
 *                  It receives the data of type [T] as a parameter.
 * @param onError A lambda function to be executed when the [Resource] is in the [Resource.Error] state.
 *                It receives the error message as a parameter.
 * @throws NullPointerException if the resource is Success but contains null data
 *
 * Example:
 * ```kotlin
 * flowOf(
 *     Resource.Loading(),
 *     Resource.Success(data = "Data"),
 *     Resource.Error(message = "An error occurred")
 * ).collectResource(
 *     onLoading = { println("Loading...") },
 *     onSuccess = { data -> println("Success: $data") },
 *     onError = { message -> println("Error: $message") }
 * )
 * ```
 */
suspend inline fun <T> Flow<Resource<T>>.collectResource(
    crossinline onLoading: () -> Unit,
    crossinline onSuccess: (T) -> Unit,
    crossinline onError: (String) -> Unit
){
    collect { resource ->
        when (resource){
            is Resource.Loading -> onLoading()
            is Resource.Success -> onSuccess(resource.data ?: return@collect)
            is Resource.Error -> onError(resource.message ?: "Unknown error")
        }
    }
}

fun <T> MutableStateFlow<T>.updateState(update: T.() -> T){
    this.value = this.value.update()
}

fun <T> MutableLiveData<T>.updateState(update: T.() -> T){
    this.value = this.value?.update()
}
