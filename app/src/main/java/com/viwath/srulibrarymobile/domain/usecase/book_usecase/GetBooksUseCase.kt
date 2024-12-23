package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.exception.CoreException
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<Resource<List<BookDto>>> = flow {
        emit(Resource.Loading())
        try {
            val books = repository.getBooks()
            Log.d("GetBooksUseCase", "invoke: $books")
            emit(Resource.Success(books))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
            throw CoreException("${e.message}")
        }catch (e: HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An HTTP error occurred."))
        }catch (e: IOException){
            emit(Resource.Error("Couldn't reach the server. Check your connection."))
        }
    }
}