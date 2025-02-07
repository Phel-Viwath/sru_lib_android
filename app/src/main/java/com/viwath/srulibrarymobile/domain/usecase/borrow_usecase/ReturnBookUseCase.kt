/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.borrow_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.StudentId
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Use case for returning a book.
 *
 * This class encapsulates the logic for returning a book to the library. It interacts with the
 * [CoreRepository] to perform the actual return operation and handles potential errors, such as
 * network issues or server errors.
 *
 * @property repository The [CoreRepository] instance responsible for interacting with the data layer.
 */
class ReturnBookUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(studentId: StudentId, bookId: BookId): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.returnBook(studentId, bookId)
            Log.d("ReturnBookUseCase", "invoke: ${response.code()} ${response.message()}")
            if (response.isSuccessful)
                emit(Resource.Success(Unit))
            else
                emit(Resource.Error(response.code().toString() + response.message()))
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