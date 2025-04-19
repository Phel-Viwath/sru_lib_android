/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

object PermissionLauncher {

    fun Fragment.cameraPermissionLauncher(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): ActivityResultLauncher<String>{
        return this@cameraPermissionLauncher.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted ->
            if (isGranted) onGranted() else onDenied()
        }
    }

    fun Fragment.storagePermissionLauncher(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): ActivityResultLauncher<String> {
        return this@storagePermissionLauncher.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted ->
            if (isGranted) onGranted() else onDenied()
        }
    }



}