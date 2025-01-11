/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCollegeUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<Resource<List<College>>> = flow{
        emit(Resource.Loading())
        try {
            val colleges = repository.college()
            Log.d("GetCollegeUseCase", "invoke: $colleges")
            emit(Resource.Success(colleges))
        }catch (e: Exception){
            emit(Resource.Error(e.message.toString()))
        }
    }
}