/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.borrow_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow
import com.viwath.srulibrarymobile.domain.model.borrow.toBorrow
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Use case responsible for retrieving all borrow records.
 *
 * This class encapsulates the logic for fetching a list of all [Borrow] objects
 * from the underlying [CoreRepository]. It handles potential errors during the
 * data retrieval process, such as network issues or server errors, and emits
 * the result as a [Resource] sealed class.
 *
 * @property repository The [CoreRepository] instance used to interact with the data layer.
 */
class GetAllBorrowUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<Resource<List<Borrow>>> = flow{
        emit(Resource.Loading())
        try {
            val borrows = repository.getAllBorrowsDetail().map { it.toBorrow() }
            emit(Resource.Success(borrows))
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