/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

import com.viwath.srulibrarymobile.domain.model.entry.Entry

sealed class QrEntryEvent {
    data class LoadStudent(val studentId: String) : QrEntryEvent()
    data class SaveAttention(val studentId: String, val purpose: String) : QrEntryEvent()
    data class LoadRecentEntry(val entry: Entry): QrEntryEvent()
}