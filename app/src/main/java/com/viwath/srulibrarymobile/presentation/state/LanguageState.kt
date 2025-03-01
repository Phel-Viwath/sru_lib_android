/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state

import com.viwath.srulibrarymobile.domain.model.Language

data class LanguageState(
    val isLoading: Boolean = false,
    val languages: List<Language> = emptyList(),
    val error: String = ""
)
