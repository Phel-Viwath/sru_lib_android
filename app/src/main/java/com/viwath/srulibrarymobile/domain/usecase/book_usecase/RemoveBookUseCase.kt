/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveBookUseCase @Inject constructor(
    private val repository: CoreRepository
){
    operator fun invoke(bookId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.moveToTrash(bookId)
            if (result) emit(Resource.Success(true))
            else emit(Resource.Error("Error move to trash"))
        } catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: ""))
        }
    }
}