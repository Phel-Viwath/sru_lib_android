/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(bookDto: BookDto): Flow< Resource<Unit>> = flow {
        emit(Resource.Loading(Unit))
        try {
            val result = repository.addBooks(listOf(bookDto))
            if (result) emit(Resource.Success(Unit))
            else emit(Resource.Error("Upload error. Please check ID."))
        } catch (e: HttpException) {
            Log.d("AddBookUseCase", "invoke: ${e.message} ${e.cause}")
            emit(Resource.Error(e.localizedMessage ?: "An HTTP error occurred."))
        } catch (e: IOException) {
            Log.d("AddBookUseCase", "invoke: ${e.message} ${e.cause}")
            emit(Resource.Error("Couldn't reach the server. Check your connection."))
        }
    }
}
