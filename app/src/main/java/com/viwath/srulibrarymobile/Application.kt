package com.viwath.srulibrarymobile

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.viwath.srulibrarymobile.utils.SettingPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application(){
    private lateinit var settingPreferences: SettingPreferences
    override fun onCreate() {
        super.onCreate()
        settingPreferences = SettingPreferences(applicationContext)
        val themeMode = settingPreferences.getSavedTheme()
        val theme = when(themeMode){
            0 -> AppCompatDelegate.MODE_NIGHT_NO
            1 -> AppCompatDelegate.MODE_NIGHT_YES
            2 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
        Log.d("Application", "onCreate: $theme, mode: $themeMode")
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}