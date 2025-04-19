/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.datastore

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.viwath.srulibrarymobile.utils.KeyStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_preferences")

class DataStoreHelper private constructor(
    context: Context,
    private val keyStoreManager: KeyStoreManager
) {

    val dataStore = context.dataStore

    companion object{
        @Volatile private var instance: DataStoreHelper? = null
        fun getInstance(context: Context, keyStoreManager: KeyStoreManager): DataStoreHelper{
           return instance ?: synchronized(this) {
               instance ?: DataStoreHelper(context.applicationContext, keyStoreManager).also { instance = it }
           }
        }
    }

    suspend fun <T> savePrefs(key: Preferences.Key<T>, value: T){
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun saveEncryptedPrefs(key: Preferences.Key<String>, value: String){
        val encryptedByte = keyStoreManager.encrypt(value.toByteArray())
        val encryptedString = Base64.encodeToString(encryptedByte, Base64.DEFAULT)
        dataStore.edit { preferences ->
            preferences[key] = encryptedString
        }
    }

    fun <T> readPrefs(key: Preferences.Key<T>): Flow<T?> {
        return dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    fun readEncryptedPrefs(key: Preferences.Key<String>): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[key]?.let { encryptedString ->
                val encryptedBytes = Base64.decode(encryptedString, Base64.DEFAULT)
                val decryptedBytes = keyStoreManager.decrypt(encryptedBytes)
                val decryptedString = String(decryptedBytes)
                decryptedString
            }
        }
    }

    suspend fun <T> clearValue(vararg key: Preferences.Key<T>){
        dataStore.edit { preferences ->
            key.forEach {
                preferences.remove(it)
            }
        }
    }

    suspend fun <T> clearEncryptedValue(vararg key: Preferences.Key<T>){
        dataStore.edit { preferences ->
            key.forEach {
                preferences.remove(it)
            }
        }
    }

}