/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

@file:Suppress("DEPRECATION")

package com.viwath.srulibrarymobile.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import eightbitlab.com.blurview.BlurView
import androidx.core.graphics.drawable.toDrawable

fun BlurView.applyBlur(
    activity: Activity,
    r: Float = 5f,
    translucentColor: Int
){
    val rootView = activity.window.decorView as ViewGroup
    val windowBackground = activity.window.decorView.background ?: Color.TRANSPARENT.toDrawable()

    val blurAlgorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        eightbitlab.com.blurview.RenderEffectBlur()
    } else {
        eightbitlab.com.blurview.RenderScriptBlur(activity)
    }

    this@applyBlur.apply{
        setupWith(rootView, blurAlgorithm)
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(r)
            .setBlurAutoUpdate(true)
            .setBlurEnabled(true)
            .setOverlayColor(translucentColor)

        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true
    }

}