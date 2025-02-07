/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.book.Book
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * `UpdateBookUseCase` is a use case class responsible for updating a book's information.
 *
 * It interacts with the [CoreRepository] to perform the update operation and emits
 * a [Resource] object representing the outcome of the operation.
 *
 * This use case handles potential errors such as network issues ([IOException]) and
 * HTTP errors ([HttpException]) and provides appropriate error messages.
 *
 * @property repository The [CoreRepository] instance used to interact with the data layer.
 *
 * @constructor Creates an instance of `UpdateBookUseCase` with the provided repository.
 */
class UpdateBookUseCase @Inject constructor(
    private val repository: CoreRepository
){
    operator fun invoke(book: Book): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.updateBook(book)
            if (result)
                emit(Resource.Success(true))
            else {
                emit(Resource.Error("Update error!"))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An HTTP error occurred."))
        }catch (e: IOException){
            emit(Resource.Error("Couldn't reach the server. Check your connection."))
        }
    }
}