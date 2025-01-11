/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<Resource<List<Language>>> = flow {
        emit(Resource.Loading())
        try{
            val languages = repository.bookLanguages()
            Log.d("GetLanguageUseCase", "invoke: $languages")
            emit(Resource.Success(languages))
        }catch (e: Exception){
            emit(Resource.Error(e.message.toString()))
        }
    }
}