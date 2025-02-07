/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.borrow_usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.BorrowId
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * `ExtendBorrowUseCase` is a use case responsible for extending the duration of a borrowed item.
 *
 * It interacts with the [CoreRepository] to perform the extension operation.
 *
 * @property repository The [CoreRepository] instance used to communicate with the data layer.
 */
class ExtendBorrowUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(id: BorrowId): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val extend = repository.extendBorrow(id)
            if (extend.isSuccessful){
                emit(Resource.Success(Unit))
            }else{
                emit(Resource.Error(extend.message() + extend.code()))
            }
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