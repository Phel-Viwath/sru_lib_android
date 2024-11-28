package com.viwath.srulibrarymobile.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun putString(key: String, values: String){
        prefs.edit().putString(key, values).apply()
    }
    fun getString(key: String, defaultValue: String? = null): String? {
        return prefs.getString(key, defaultValue)
    }

    fun putInt(key: String, values: Int) {
        prefs.edit().putInt(key, values).apply()
    }
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun clearValue(vararg key: String) {
        key.forEach {
            prefs.edit().remove(it).apply()
        }

    }

    companion object{
        private const val PREFS_NAME = "prefs_name"
        private var instance: SharedPreferencesHelper? = null
        fun getInstance(context: Context): SharedPreferencesHelper {
            if (instance == null){
                return SharedPreferencesHelper(context.applicationContext)
            }
            return instance!!
        }
    }
}