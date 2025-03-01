/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.viwath.srulibrarymobile.utils.share_preferences.SettingPreferences
import com.viwath.srulibrarymobile.utils.share_preferences.TokenManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LibApp : Application(){
    private lateinit var settingPreferences: SettingPreferences
    private lateinit var tokenManager: TokenManager
    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(applicationContext)
        settingPreferences = SettingPreferences(applicationContext)
        val themeMode = settingPreferences.getSavedTheme()
        val theme = when(themeMode){
            0 -> AppCompatDelegate.MODE_NIGHT_NO
            1 -> AppCompatDelegate.MODE_NIGHT_YES
            2 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}