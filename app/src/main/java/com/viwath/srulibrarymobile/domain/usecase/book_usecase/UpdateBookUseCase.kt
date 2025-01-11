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
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(
    private val repository: CoreRepository
){
    suspend operator fun invoke(bookDto: BookDto): Resource<Boolean> {
        Resource.Loading(Unit)
        return try {
            val result = repository.updateBook(bookDto)
            if (result)
                Resource.Success(true)
            else
                Resource.Error("Update error!")
        }catch (e: HttpException){
            Resource.Error(e.localizedMessage ?: "An HTTP error occurred.")
        }catch (e: IOException){
            Resource.Error("Couldn't reach the server. Check your connection.")
        }
    }
}