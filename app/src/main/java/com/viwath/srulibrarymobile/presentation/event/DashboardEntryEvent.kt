package com.viwath.srulibrarymobile.presentation.event

sealed class DashboardEntryEvent {
    data class GetStudent(val studentId: String): DashboardEntryEvent()
    data class SaveAttend(val studentId: String, val purpose: String) : DashboardEntryEvent()
}