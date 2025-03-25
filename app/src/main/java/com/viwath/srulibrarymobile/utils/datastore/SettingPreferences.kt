/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.datastore

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.viwath.srulibrarymobile.utils.KeyStoreManager
import kotlinx.coroutines.flow.firstOrNull

/**
 * `SettingPreferences` is a class responsible for managing application settings,
 * specifically for storing and retrieving the user's preferred theme.
 * It utilizes `SharedPreferencesHelper` for underlying data persistence.
 *
 * @property context The application context, used for accessing shared preferences.
 */
class SettingPreferences(context: Context, keyStoreManager: KeyStoreManager) {

    private val prefers = DataStoreHelper.getInstance(context, keyStoreManager)

    suspend fun saveTheme(values: Int){
        prefers.savePrefs(THEME, values)
    }
    suspend fun getSavedTheme(): Int {
        return prefers.readPrefs(THEME).firstOrNull() ?: 0
    }

    suspend fun saveViewMode(values: String){
        prefers.savePrefs(VIEW_MODE, values)
    }

    suspend fun getViewMode(): String{
        return prefers.readPrefs(VIEW_MODE).firstOrNull() ?: "classic"
    }

    companion object{
        private val THEME = intPreferencesKey("theme")
        private val VIEW_MODE = stringPreferencesKey("view_mode")
    }

}