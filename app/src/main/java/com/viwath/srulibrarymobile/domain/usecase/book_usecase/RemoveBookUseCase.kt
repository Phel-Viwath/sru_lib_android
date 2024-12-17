package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import javax.inject.Inject

class RemoveBookUseCase @Inject constructor(
    private val repository: CoreRepository
){
    suspend operator fun invoke(bookId: String): Resource<Boolean> = try {
        Resource.Loading(Unit)
        val result = repository.moveToTrash(bookId)
        if (result) Resource.Success(true)
        else Resource.Error("Error move to trash")
    }catch (e: Exception){
        Resource.Error(e.localizedMessage ?: "")
    }
}