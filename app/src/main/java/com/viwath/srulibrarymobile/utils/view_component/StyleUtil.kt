/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.view_component

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.domain.model.Message

fun Context.getTranslucentColor(isDarkMode: Boolean): Int {
    return if (isDarkMode)
        ContextCompat.getColor(this, R.color.translucent_black_20)
    else ContextCompat.getColor(this, R.color.translucent_white_20)
}

fun Context.getTransparent(): Int = this.getColor(android.R.color.transparent)

fun showSnackbar(
    view: View,
    message: Message,
    icon: Drawable? = null,
    @ColorInt colorTint: Int = 0
){
    if (icon != null && colorTint != 0)
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setIcon(icon, colorTint)
            .show()
    else Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun Snackbar.setIcon(drawable: Drawable, @ColorInt colorTint: Int): Snackbar{
    return this.apply {
        setAction(""){}
        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        textView.text = ""
        drawable.setTint(colorTint)
        drawable.setTintMode(android.graphics.PorterDuff.Mode.SRC_ATOP)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }
}

fun Context.showToast(message: Message){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showDialog(title: String, message: Message){
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(true)
        .setPositiveButton("OK") {dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}

fun Context.showDialog(
    title: String,
    message: Message,
    onPositiveClick: () -> Unit,
){
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(true)
        .setPositiveButton("OK") {dialog, _ ->
            onPositiveClick()
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}