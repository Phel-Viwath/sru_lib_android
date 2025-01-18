/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.borrow_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.borrow.BorrowRequest
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BorrowBookUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(
        borrowRequest: BorrowRequest
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val result = repository.borrowBook(borrowRequest)
            if (result) emit(Resource.Success(Unit))
            else emit(Resource.Error("Borrow error."))
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