package com.viwath.srulibrarymobile.data.repository

import android.util.Log
import com.viwath.srulibrarymobile.common.exception.CoreException
import com.viwath.srulibrarymobile.data.api.CoreApi
import com.viwath.srulibrarymobile.domain.model.Attend
import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.domain.model.dashboard.Dashboard
import com.viwath.srulibrarymobile.domain.model.entry.Entry
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import javax.inject.Inject

class CoreRepositoryImp @Inject constructor(
    private val api: CoreApi,
) : CoreRepository {
    override suspend fun getDashboard(): Dashboard {
        val response = api.dashboard()
        return if (response.isSuccessful){
            response.body()?.let {
                Dashboard(
                    it.cardData,
                    it.totalMajorVisitor,
                    it.weeklyVisitor,
                    it.bookAvailable,
                    it.customEntry
                )
            } ?: throw CoreException("Response body is null")
        }
        else throw CoreException("Error: Dashboard")
    }

    override suspend fun getStudentById(id: Long): Students {
        val response = api.getStudentById(id)
        if (response.code() == 404) throw CoreException("Not Found")
        return if (response.isSuccessful){
            response.body()?.let {
                Students(
                    studentId = it.studentId,
                    studentName = it.studentName,
                    gender = it.gender,
                    dateOfBirth = it.dateOfBirth,
                    degreeLevel = it.degreeLevel,
                    majorName = it.majorName,
                    generation = it.generation
                )
            } ?: throw CoreException("Response body is null")
        }
        else throw CoreException("Error: get student by id")
    }

    override suspend fun newAttend(studentId: String, purpose: String): Attend {
        val response = api.newAttend(studentId, purpose)
        if (!response.isSuccessful)
            throw CoreException("Error: new attend ${response.code()} + ${response.body()}")
        return response.body()?.let {
            Attend(
                attendId = it.attendId,
                studentId = it.studentId,
                entryTimes = it.entryTimes,
                exitingTimes = it.exitingTimes,
                date = it.date,
                purpose = it.purpose
            )
        } ?: throw CoreException("new attend null")
    }
    override suspend fun getRecentEntryData(): Entry {
        val response = api.recentEntryData()
        return if (response.isSuccessful){
            response.body()?.let {
                Entry(
                    cardEntry = it.cardEntry,
                    attendDetail = it.attendDetail
                )
            } ?: throw CoreException("Body is null")
        } else throw CoreException("")
    }

    override suspend fun updateExitingTime(studentId: Long): Boolean {
        val response = api.updateExitingTime(studentId)
        if (response.isSuccessful) return true
        else throw CoreException("${response.body()} ${response.code()}")
    }

    override suspend fun checkExitingAttend(id: String): String {
        val response = api.checkExistingStudent(id)
        if (!response.isSuccessful)
            throw CoreException("Network Error!, ${response.code()} + $id + ${response.message()}")
        val body = response.body()
        Log.d("CheckExitingAttend", "Response body: $body")
        return body?.status ?: throw CoreException("Response body is null")
    }

}