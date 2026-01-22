/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.viwath.srulibrarymobile.utils.KeyStoreManager
import com.viwath.srulibrarymobile.utils.datastore.SettingPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class LibApp : Application(){
    private lateinit var settingPreferences: SettingPreferences
    override fun onCreate() {
        super.onCreate()

        settingPreferences = SettingPreferences(applicationContext, KeyStoreManager())

        CoroutineScope(Dispatchers.IO).launch {
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
}