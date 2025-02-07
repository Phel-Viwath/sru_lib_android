/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Helper class for handling permission requests in an Android Fragment.
 *
 * This class provides methods to check if certain permissions are granted
 * and can be extended to request and handle permission results.
 *
 * @property fragment The Fragment instance associated with the permission request.
 */
class PermissionRequest(private val fragment: Fragment) {

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasReadStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 12 and below
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

//    fun requestPermissions(vararg permissions: String, requestCode: Int) {
//        ActivityCompat.requestPermissions(
//            fragment.requireActivity(),
//            permissions,
//            requestCode
//        )
//    }

//    fun handlePermissionResult(
//        requestCode: Int,
//        permissions: Array<out String?>,
//        grantResults: IntArray,
//        onPermissionGranted: () -> Unit,
//        onPermissionDenied: () -> Unit
//    ) {
//        if (grantResults.isNotEmpty()) {
//            // Check if all permissions are granted
//            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
//            if (allGranted) {
//                onPermissionGranted()
//            } else {
//                onPermissionDenied()
//            }
//        } else {
//            // No permissions were granted
//            onPermissionDenied()
//        }
//    }
}
