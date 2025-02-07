/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.common

import android.app.Activity
import android.app.AlertDialog
import com.viwath.srulibrarymobile.R

/**
 * `Loading` is a utility class that provides a simple way to display and dismiss a loading dialog.
 *
 * This class uses an `AlertDialog` to show a custom loading view (defined in `R.layout.layout_loading`)
 * with a transparent background and no ability to cancel by the user.
 *
 * @property activity The `Activity` context required to create the `AlertDialog`.
 */
class Loading(private val activity: Activity) {
    private var dialog: AlertDialog? = null

    fun loadingStart(){
        if (dialog?.isShowing == true) return
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.layout_loading, null)
        builder.setView(view)
        dialog = builder.create()
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    fun loadingDismiss(){
        dialog?.takeIf { it.isShowing }?.dismiss()
        dialog = null
    }


}