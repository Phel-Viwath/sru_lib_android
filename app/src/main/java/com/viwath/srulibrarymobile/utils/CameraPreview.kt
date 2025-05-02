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
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.viwath.srulibrarymobile.utils.qr_reader.QRCodeAnalyzer

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
                    it.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QRCodeAnalyzer(resultHandler)
                    )
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

            // Clear preview by reattaching the view
            val parent = previewView.parent as? ViewGroup
            val index = parent?.indexOfChild(previewView) ?: -1
            if (parent != null && index != -1) {
                parent.removeView(previewView)
                parent.addView(previewView, index)
            }

        }, ContextCompat.getMainExecutor(context))
    }

}
