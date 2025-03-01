/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.usecase.book_usecase

import android.util.Log
import com.viwath.srulibrarymobile.domain.HandleDataError.handleRemoteError
import com.viwath.srulibrarymobile.domain.Result
import com.viwath.srulibrarymobile.domain.repository.CoreRepository
import com.viwath.srulibrarymobile.presentation.state.book_state.UploadState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.IOException
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

/**
 * Use case responsible for uploading a book file to the server.
 *
 * This class handles the entire process of uploading a file, including:
 * - Validating the file's existence and readability.
 * - Creating a request body with the appropriate content type.
 * - Reporting upload progress to the caller.
 * - Sending the file to the server via the [CoreRepository].
 * - Handling various potential errors during the upload process (e.g., HTTP errors, network issues).
 * - Emitting different states of the upload process through a [Flow] of [UploadState].
 *
 * @property repository The [CoreRepository] instance used to communicate with the server.
 */
class UploadBookUseCase @Inject constructor(
    private val repository: CoreRepository
) {
    operator fun invoke(
        file: File
    ): Flow<UploadState> = channelFlow{
        send(UploadState.Uploading(0, 0, file.length()))
        Log.d("UploadBookUseCase", "invoke: UploadBookUseCase called. File path: ${file.path}")

        if (!file.exists() || !file.canRead()) {
            Log.e("UploadBookUseCase", "File does not exist or is not readable: ${file.path}")
            send(UploadState.Error("Invalid file. Please check the file and try again."))
            return@channelFlow
        }
        try {
            val progressChannel = Channel<UploadState.Uploading>(capacity = Channel.BUFFERED)
            Log.d("UploadBookUseCase", "File is valid. Proceeding with upload...")
            // Create a RequestBody for the file
            val requestBody = object : RequestBody() {
                override fun contentLength(): Long = file.length()
                override fun contentType(): MediaType? = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull()

                override fun writeTo(sink: BufferedSink) {
                    Log.d("UploadBookUseCase", "writeTo: Upload started.")
                    var uploadedBytes = 0L
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)


                    file.inputStream().use { input ->
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            sink.write(buffer, 0, read)
                            uploadedBytes += read
                            val progress = ((uploadedBytes * 100) / contentLength()).toInt()
                            Log.d("UploadBookUseCase", "Uploaded: $uploadedBytes / ${contentLength()} ($progress%)")
                            trySend(
                                UploadState.Uploading(
                                    progress = progress.coerceIn(0, 85),
                                    uploadedSize = uploadedBytes,
                                    totalSize = contentLength()
                                )
                            ).isSuccess
                        }
                    }
                    progressChannel.close()
                }
            }
            coroutineScope {
                launch {
                    progressChannel.consumeAsFlow().collect { progress ->
                        Log.d("UploadBookUseCase", "Progress collected: $progress")
                        send(progress)
                    }
                }

                val multipartBody = MultipartBody.Part.createFormData(
                    name = "book_file",
                    filename = file.name,
                    body = requestBody
                )
                Log.d("UploadBookUseCase", "About to upload file to server")
                when(val response = repository.uploadBook(multipartBody)){
                    is Result.Success -> {
                        send(UploadState.Uploading(100, file.length(), file.length()))
                        send(UploadState.Success("Upload Success!"))
                    }
                    is Result.Error -> {
                        send(UploadState.Error(response.error.handleRemoteError()))
                    }
                }
            }
        }catch (e: HttpException) {
            Log.e("UploadBookUseCase", "HTTP error: ${e.message}", e)
            send(UploadState.Error(e.localizedMessage ?: "An HTTP error occurred."))
        } catch (e: IOException) {
            Log.e("UploadBookUseCase", "Network error: ${e.message}", e)
            send(UploadState.Error("Couldn't reach the server. Check your connection."))
        } catch (e: Exception) {
            Log.e("UploadBookUseCase", "Unexpected error: ${e.message}", e)
            send(UploadState.Error("An unexpected error occurred."))
        }
    }.flowOn(IO)

}
