package com.viwath.srulibrarymobile.common

import android.app.Activity
import android.app.AlertDialog
import com.viwath.srulibrarymobile.R

class Loading(private val activity: Activity) {
    private var dialog: AlertDialog? = null

    fun loadingStart(){
        if (dialog?.isShowing == true) return
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.layout_loading, null)
        builder.setView(view)
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    fun loadingDismiss(){
        dialog?.takeIf { it.isShowing }?.dismiss()
        dialog = null
    }


}