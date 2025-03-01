/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase

import com.viwath.srulibrarymobile.common.result.Resource
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case responsible for retrieving the list of available book languages.
 *
 * This class interacts with the [CoreRepository] to fetch the languages and
 * handles potential errors during the process. It exposes the results as a
 * [Flow] of [Resource] objects, allowing for easy handling of loading, success,
 * and error states.
 *
 * @property repository The [CoreRepository] instance used to access the data layer.
 * @constructor Creates a [GetLanguageUseCase] with the specified [CoreRepository].
 */
class GetLanguageUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(): Flow<Resource<List<Language>>> = flow {
        emit(Resource.Loading())
        when(val result = repository.bookLanguages()){
            is Result.Success -> emit(Resource.Success(result.data))
            is Result.Error -> emit(Resource.Error(result.error.handleRemoteError()))
        }
    }
}