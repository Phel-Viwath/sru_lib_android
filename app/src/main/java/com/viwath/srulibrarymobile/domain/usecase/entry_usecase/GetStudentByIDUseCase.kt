package com.viwath.srulibrarymobile.domain.usecase.entry_usecase

import android.util.Log
import com.viwath.srulibrarymobile.common.result.CoreResult
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStudentByIDUseCase @Inject constructor(
    private val repository: CoreRepository
){
    operator fun invoke(id: Long): Flow<CoreResult<Students>> = flow {
        emit(CoreResult.Loading())
        try{
            val student = repository.getStudentById(id)
            Log.d("Invoke Student", "invoke: $student")
            emit(CoreResult.Success(student))
        }catch (e: Exception){
            Log.d("Error fetch student", "invoke: ${e.printStackTrace()}")
            emit(CoreResult.Error(e.localizedMessage ?: "An error occurred."))
        }
    }
}