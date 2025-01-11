/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.event

sealed class ResultEvent {
    data class ShowSuccess(val message: String) : ResultEvent()
    data class ShowError(val errorMsg: String) : ResultEvent()
}