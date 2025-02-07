/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * `GetBookInTrashUseCase`
 *
 * This class represents a use case for retrieving a list of books that are marked as being in the trash.
 * It interacts with the [CoreRepository] to fetch the data and returns a [Flow] emitting a [Resource]
 * indicating the state of the operation (loading, success, or error) along with the list of [BookDto]
 * if the operation was successful.
 *
 * @property repository The [CoreRepository] instance responsible for data access.
 *
 * Usage:
 *  1. Inject this class into the ViewModel or Presenter.
 *  2. Call the [invoke] operator function to get the [Flow].
 *  3. Collect the [Flow] to receive updates about the data state and the list of books.
 *
 * Example:
 * ```kotlin
 * viewModelScope.launch {
 *     getBookInTrashUseCase().collect { resource ->
 *         when (resource) {
 *             is Resource.Loading -> // Show loading state
 *             is Resource.Success -> {
 *                 val booksInTrash = resource.data
 *                 // Update UI with booksInTrash
 *             }
 *             is Resource.Error -> {
 *                  val errorMessage = resource.message
 *                  // Show error message
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * Error Handling:
 * The use case handles several potential error scenarios:
 * - General exceptions: Any unhandled exception will result in an [Resource.Error] with a generic error message.
 * - [HttpException]: Errors related to HTTP requests will result in [Resource.Error] with the localized error message or a generic HTTP error message.
 * - [IOException]: Network-related errors will result in [Resource.Error] with a message suggesting to check the internet connection.
 */
class GetBookInTrashUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke() : Flow<Resource<List<BookDto>>> = flow {
        emit(Resource.Loading())
        try {
            val books = repository.getBooksInTrash().toList()
            emit(Resource.Success(books))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
        } catch (e: HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An HTTP error occurred."))
        } catch (e: IOException){
            emit(Resource.Error("Couldn't reach the server. Check your connection."))
        }
    }
}