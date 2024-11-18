package com.viwath.srulibrarymobile.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionRequest(private val fragment: Fragment){

    fun hasCameraPermission(): Boolean{
        return ActivityCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(requestCode: Int){
        ActivityCompat.requestPermissions(
            fragment.requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            requestCode
        )
    }

    fun handlePermissionResult(
        requestCode: Int,
        grandResult: IntArray,
        onPermissionGraded: () -> Unit
    ){
        if (requestCode == 0 && grandResult.isNotEmpty() && grandResult[0] == PackageManager.PERMISSION_GRANTED)
            onPermissionGraded()
    }
}