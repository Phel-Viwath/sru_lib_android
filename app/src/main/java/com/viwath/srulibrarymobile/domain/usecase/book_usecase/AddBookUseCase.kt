package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val repository: CoreRepository
){
    suspend operator fun invoke(bookDto: List<BookDto>): Resource<Unit> = try {
        Resource.Loading(Unit)
        repository.addBooks(bookDto)
        Resource.Success(Unit)
    }catch (e: HttpException){
        Resource.Error(e.localizedMessage ?: "An HTTP error occurred.")
    }catch (e: IOException){
        Resource.Error("Couldn't reach the server. Check your connection.")
    }
}
