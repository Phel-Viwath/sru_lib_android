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

/**
 * Use case for removing a book from the library by moving it to the trash.
 *
 * This class encapsulates the logic for removing a book based on its ID.
 * It interacts with the [CoreRepository] to perform the move-to-trash operation.
 *
 * @property repository The [CoreRepository] instance responsible for data access.
 */
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