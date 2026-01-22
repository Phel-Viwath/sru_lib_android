/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.qr_reader

import android.util.Log
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult

class QrCodeCallback(
    private val beepManager: BeepManager,
    private val onQrCodeScanned: (String) -> Unit,
    private val onScanError: (Exception) -> Unit,
    private val onErrorFormat:(String) -> Unit
) : BarcodeCallback {

    private var isProcessing = false

    override fun barcodeResult(result: BarcodeResult?) {
        if (isProcessing)
            return
        isProcessing = true

        try {
            beepManager.playBeepSoundAndVibrate()
            val qrText = result?.text
            Log.d("QrCodeCallBack", "barcodeResult: $qrText")
            qrText?.let {
                if (!it.contains("=")){
                    onErrorFormat("Invalid QR code format")
                    throw IllegalArgumentException("Invalid QR code format: $qrText")
                }
                onQrCodeScanned(it)
            }
        }catch (e: Exception){
            Log.e("QrCodeCallBack", "Error processing QR code: ${e.message}", e)
            onScanError(e)
        }
    }

    fun reset() {
        isProcessing = false
    }

}