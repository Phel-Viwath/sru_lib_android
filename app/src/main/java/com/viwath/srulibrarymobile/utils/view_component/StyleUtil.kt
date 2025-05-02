/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.view_component

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R

fun Context.getTranslucentColor(isDarkMode: Boolean): Int {
    return if (isDarkMode)
        ContextCompat.getColor(this, R.color.translucent_black_20)
    else ContextCompat.getColor(this, R.color.translucent_white_20)
}

fun Context.getTransparent(): Int = this.getColor(android.R.color.transparent)

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