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

/**
 * Converts a given [Uri] to a temporary [File] object.
 *
 * This function takes a [Uri] representing a file (e.g., from content providers) and creates a temporary
 * [File] in the application's cache directory. The content from the [Uri] is copied into this temporary file.
 *
 * @param context The application context, needed to access the content resolver and cache directory.
 * @return A [File] object representing the temporary file containing the data from the [Uri], or `null` if:
 *         - The file name could not be extracted from the [Uri].
 *         - There was an error opening the input stream from the [Uri].
 *         - There was a problem writing the data to the temporary file.
 * @throws FileNotFoundException if the input stream from the [Uri] cannot be opened.
 *
 * Example Usage:
 * ```
 * val uri: Uri = ... // Some Uri
 * val file = uri.uriToFile(applicationContext)
 * if (file != null) {
 *     // Use the file
 *     println("File path: ${file.absolutePath}")
 * } else {
 *     // Handle error
 *     println("Failed to convert Uri to File")
 * }
 * ```
 */
fun Uri.uriToFile(context: Context): File?{
    val contentResolver = context.contentResolver
    val fileName = this.getFileNameFromUri(context) ?: return null
    val tempFile = File(context.cacheDir, fileName)
    contentResolver.openInputStream(this)?.use { inputStream ->
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream) // Copy content to the temp file
        }
    }
    return tempFile
}

/**
 * Retrieves the file name from a given content URI.
 *
 * This function queries the content resolver using the provided URI to extract the file's display name.
 *
 * @param context The application context.
 * @param uri The content URI of the file.
 * @return The file name (display name) associated with the URI, or null if the file name cannot be determined.
 */
fun Uri.getFileNameFromUri(context: Context): String? {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    return cursor?.use {
        if (it.moveToFirst()) {
            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                it.getString(displayNameIndex)
            } else null
        } else null
    }
}
