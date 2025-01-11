/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

// Class to handle camera preview and QR code scanning functionality.
class CameraPreview(
    private val context: Context, // Application or activity context
    private var previewView: PreviewView, // View for displaying camera feed
    private val lifecycleOwner: LifecycleOwner // LifecycleOwner to manage camera lifecycle
) {

    private var resultHandler: ((String) -> Unit)? = null // Callback for handling QR code scan results
    private var cameraControl: CameraControl? = null // Used for controlling camera features like flashlight

    // Starts the camera and sets up QR code analysis
    fun startCamera(resultHandler: (String) -> Unit) {
        this.resultHandler = resultHandler // Store the result callback

        // Get a CameraProvider instance asynchronously
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get() // Retrieve the CameraProvider

            // Set up the Preview use case to display the camera feed
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider // Attach PreviewView as the display surface
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // Use the back camera

            // Set up ImageAnalysis use case for analyzing frames for QR codes
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Drop old frames
                .build()
                .also {
                    // Set a QRCodeAnalyzer to process image frames
                    it.setAnalyzer(ContextCompat.getMainExecutor(context), QRCodeAnalyzer(resultHandler))
                }

            try {
                cameraProvider.unbindAll() // Unbind any existing use cases
                // Bind use cases to lifecycle and start camera
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
                cameraControl = camera.cameraControl // Store the CameraControl for controlling features
            } catch (exc: Exception) {
                Log.e("QrEntryFragment", "Use case binding failed", exc) // Log errors
            }

        }, ContextCompat.getMainExecutor(context)) // Run on the main thread
    }

    // Toggles flashlight on or off
    fun toggleFlashlight(isFlashlightOn: Boolean) {
        cameraControl?.enableTorch(isFlashlightOn) // Enable or disable flashlight
    }

    // Stops the camera and clears the preview
    fun stopCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get() // Retrieve CameraProvider
            cameraProvider.unbindAll() // Unbind all use cases
            clearPreview() // Clear and reset the PreviewView
        }, ContextCompat.getMainExecutor(context))
    }

    // Clears the preview view to reset its state
    private fun clearPreview() {
        val parent = previewView.parent as ViewGroup // Get the parent ViewGroup
        val index = parent.indexOfChild(previewView) // Get the index of PreviewView in parent
        parent.removeView(previewView) // Remove the view
        parent.addView(previewView, index) // Re-add the view at the same index
    }

    // Inner class for analyzing image frames for QR codes
    inner class QRCodeAnalyzer(private val resultHandler: (String) -> Unit) : ImageAnalysis.Analyzer {

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
}
