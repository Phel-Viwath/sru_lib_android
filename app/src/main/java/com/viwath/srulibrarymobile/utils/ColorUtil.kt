/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.viwath.srulibrarymobile.R

fun Context.getTranslucentColor(isDarkMode: Boolean): Int {
    return if (isDarkMode)
        ContextCompat.getColor(this, R.color.translucent_black_20)
    else ContextCompat.getColor(this, R.color.translucent_white_20)
}

fun Context.getTransparent(): Int = this.getColor(android.R.color.transparent)