/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.qr_reader

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

// Inner class for analyzing image frames for QR codes
class QRCodeAnalyzer(private val resultHandler: (String) -> Unit) : ImageAnalysis.Analyzer {

    // QR code reader from ZXing library, configured for QR codes
    private val reader = MultiFormatReader().apply {
        setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
    }

    // Analyzes a single image frame for QR codes
    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer // Get image data from the first plane
        val data = buffer.toByteArray() // Convert buffer to a byte array
        val width = image.width
        val height = image.height

        // Convert image data to a format usable for QR code decoding
        val source = PlanarYUVLuminanceSource(
            data, width, height, 0, 0, width, height, false
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            // Decode the QR code and pass the result to the handler
            val result = reader.decode(bitmap)
            resultHandler(result.text)
        } catch (e: Exception) {
            // Ignore errors when no QR code is found
            Log.d("CameraPreView", "analyze: ${e.message}")
        } finally {
            image.close() // Close the image frame to free resources
        }
    }

    // Extension function to convert ByteBuffer to a ByteArray
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind() // Reset the buffer's position to zero
        val data = ByteArray(remaining()) // Create a byte array of the correct size
        get(data) // Copy the buffer's contents into the byte array
        return data
    }
}
