/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * A helper class for managing SharedPreferences, providing both regular and encrypted storage options.
 *
 * This class uses the AndroidX Security library to provide encrypted storage for sensitive data.
 * It follows the Singleton pattern to ensure a single instance is used throughout the application.
 *
 * @param context The application context. Used to access SharedPreferences.
 */
class SharedPreferencesHelper private constructor(
    context: Context
) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val encryptedPrefs = createEncryptedSharePreferences(context)

    fun putString(key: String, values: String): Unit = prefs.edit().putString(key, values).apply()
    fun putEncryptedString(key: String, values: String): Unit = encryptedPrefs.edit().putString(key, values).apply()

    fun getString(key: String, defaultValue: String? = null): String? = prefs.getString(key, defaultValue)
    fun getEncryptedString(key: String, defaultValue: String? = null): String? = encryptedPrefs.getString(key, defaultValue)

    fun putInt(key: String, values: Int): Unit =  prefs.edit().putInt(key, values).apply()
    fun punEncryptedInt(key: String, values: Int): Unit = encryptedPrefs.edit().putInt(key, values).apply()

    fun getInt(key: String, defaultValue: Int = 0): Int = prefs.getInt(key, defaultValue)
    fun getEncryptedInt(key: String, defaultValue: Int = 0): Int = encryptedPrefs.getInt(key, defaultValue)

    fun clearValue(vararg key: String): Unit = key.forEach { prefs.edit().remove(it).apply() }
    fun clearEncryptedValue(vararg key: String): Unit = key.forEach { encryptedPrefs.edit().remove(it).apply() }

    /// private

    private fun createEncryptedSharePreferences(context: Context): SharedPreferences{
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secret_share_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
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