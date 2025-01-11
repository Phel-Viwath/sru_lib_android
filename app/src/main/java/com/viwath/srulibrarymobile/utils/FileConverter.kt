/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

fun Uri.uriToFile(context: Context): File?{
    val contentResolver = context.contentResolver
    val fileName = getFileNameFromUri(context, this) ?: return null
    val tempFile = File(context.cacheDir, fileName)
    contentResolver.openInputStream(this)?.use { inputStream ->
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream) // Copy content to the temp file
        }
    }
    return tempFile
}

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        if (it.moveToFirst()) {
            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                it.getString(displayNameIndex)
            } else null
        } else null
    }
}
