/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.book.Book
import com.viwath.srulibrarymobile.domain.model.book.toBook
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for retrieving a list of active books.
 *
 * This class encapsulates the logic for fetching books from the repository,
 * filtering them to only include active books, and transforming them into
 * the desired `Book` domain model. It handles potential errors during the
 * data retrieval process and emits the result as a `Flow` of `Resource` objects.
 *
 * @property repository The repository responsible for data access. Injected via constructor injection.
 */
class GetBooksUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<Resource<List<Book>>> = flow {
        emit(Resource.Loading())
        try {
            val books = repository.getBooks()
                .filter { it.isActive }
                .map { it.toBook() }
            emit(Resource.Success(books))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            Log.d("GetBooksUseCase", "Exception: ${e.message}")
        }catch (e: HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An HTTP error occurred."))
            Log.d("GetBooksUseCase", "Exception: ${e.message}")
        }catch (e: IOException){
            emit(Resource.Error("Couldn't reach the server. Check your connection."))
            Log.d("GetBooksUseCase", "Exception: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
}