/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.share_preferences

import android.content.Context

/**
 * `SettingPreferences` is a class responsible for managing application settings,
 * specifically for storing and retrieving the user's preferred theme.
 * It utilizes `SharedPreferencesHelper` for underlying data persistence.
 *
 * @property context The application context, used for accessing shared preferences.
 */
class SettingPreferences(context: Context) {

    private val prefers = SharedPreferencesHelper.getInstance(context)

    fun saveTheme(values: Int){
        prefers.putInt(THEME, values)
    }
    fun getSavedTheme(): Int{
        return prefers.getInt(THEME)
    }

    companion object{
        private const val THEME = "theme"
    }

}