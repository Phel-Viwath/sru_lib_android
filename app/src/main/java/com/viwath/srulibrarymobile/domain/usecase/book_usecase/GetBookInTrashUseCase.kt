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
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.book.BookInTrash
import com.viwath.srulibrarymobile.domain.repository.BookRepository
import com.viwath.srulibrarymobile.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * `GetBookInTrashUseCase`
 *
 * This class represents a use case for retrieving a list of books that are marked as being in the trash.
 * It interacts with the [DashboardRepository] to fetch the data and returns a [Flow] emitting a [Resource]
 * indicating the state of the operation (loading, success, or error) along with the list of [BookDto]
 * if the operation was successful.
 *
 * @property repository The [DashboardRepository] instance responsible for data access.
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
    private val repository: BookRepository
) {
    operator fun invoke() : Flow<Resource<List<BookInTrash>>> = flow {
        emit(Resource.Loading())

        val collegeMap = when(val collegeResult = repository.college()){
            is Result.Success -> {
                collegeResult.data.toList().associateBy { it.collegeId }
            }
            is Result.Error -> {
                val error = collegeResult.error
                val message = error.handleRemoteError()
                emit(Resource.Error(message))
                emptyMap()
            }
        }
        Log.d("GetBookInTrashUseCase", "invoke: $collegeMap")

        val result = repository.getBooksInTrash()
        Log.d("GetBookInTrashUseCase", "invoke: $result")
        when(result){
            is Result.Success -> {
                val bookInTrash = result.data.mapNotNull { dto ->
                    collegeMap[dto.collegeId]?.let {
                        BookInTrash(
                            bookId = dto.bookId,
                            bookTitle = dto.bookTitle,
                            bookQuan = dto.bookQuan,
                            collegeName = it.collegeName,
                            author = dto.author,
                            publicationYear = dto.publicationYear,
                            genre = dto.genre
                        )
                    }
                }
                emit(Resource.Success(bookInTrash))
            }
            is Result.Error ->  emit(Resource.Error(result.error.handleRemoteError()))
        }
    }
}