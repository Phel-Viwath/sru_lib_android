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
