package com.viwath.srulibrarymobile.utils

import android.content.Context

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