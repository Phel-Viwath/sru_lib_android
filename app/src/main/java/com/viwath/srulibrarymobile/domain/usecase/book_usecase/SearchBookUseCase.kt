/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.book.Book
import com.viwath.srulibrarymobile.domain.model.book.toBook
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for searching books based on a keyword.
 *
 * This class encapsulates the logic for searching books in the data source
 * through the [CoreRepository]. It handles the flow of data, including
 * loading, success, and error states. It also manages potential exceptions
 * during the search process, such as network errors or HTTP errors.
 *
 * @property repository The [CoreRepository] instance responsible for interacting with the data source.
 */
class SearchBookUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(keyword: String): Flow<Resource<List<Book>>> = flow{
        emit(Resource.Loading())
        try {
            val result: List<Book> = repository.searchBook(keyword).map { it.toBook() }
            emit(Resource.Success(result))
        }catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
        } catch (e: HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An HTTP error occurred."))
        } catch (e: IOException){
            e.printStackTrace()
            emit(Resource.Error("Couldn't reach the server. Check your connection."))
        }
    }
}