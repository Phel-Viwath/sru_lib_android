/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state

import com.viwath.srulibrarymobile.domain.model.Students
import com.viwath.srulibrarymobile.domain.model.entry.Entry

sealed class QrFragmentState{
    data object Idle : QrFragmentState()
    data object Loading : QrFragmentState()
    data class StudentLoaded(val student: Students) : QrFragmentState()
    data class AttentionSaved(val message: String = "Attention saved successfully") : QrFragmentState()
    data class Error(val message: String? = null) : QrFragmentState()
    data class EntryState(val entry: Entry? = null): QrFragmentState()
}