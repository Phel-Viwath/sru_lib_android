/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.state.book_state

sealed class UploadState {
    object Idle : UploadState()
    data class Uploading(
        val progress: Int,
        val uploadedSize: Long,
        val totalSize: Long
    ) : UploadState()
    data class Success(val fileUrl: String?) : UploadState()
    data class Error(val message: String) : UploadState()
}