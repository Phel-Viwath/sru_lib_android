/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.viwath.srulibrarymobile.utils.KeyStoreManager
import kotlinx.coroutines.flow.firstOrNull

/**
 * Manages the storage and retrieval of authentication tokens and user information.
 *
 * This class provides methods to save, retrieve, and clear access tokens, refresh tokens,
 * usernames, and user roles using SharedPreferences. It utilizes the `SharedPreferencesHelper`
 * for interacting with SharedPreferences.
 *
 * @property context The Android application context used to access SharedPreferences.
 */
class UserPreferences(context: Context, keyStoreManager: KeyStoreManager) {

    private val dataStoreHelper = DataStoreHelper.getInstance(context, keyStoreManager)

    suspend fun saveAccessToken(accessToken: String): Unit =
        dataStoreHelper.saveEncryptedPrefs(ACCESS_TOKEN, accessToken)

    suspend fun saveRefreshToken(refreshToken: String): Unit =
        dataStoreHelper.saveEncryptedPrefs(REFRESH_TOKEN, refreshToken)


    suspend fun saveUsername(username: String): Unit = dataStoreHelper.savePrefs(USERNAME, username)

    suspend fun saveRole(role: String): Unit =dataStoreHelper.savePrefs(ROLE, role)

    suspend fun getAccessToken(): String? = dataStoreHelper.readEncryptedPrefs(ACCESS_TOKEN).firstOrNull()

    suspend fun getRefreshToken(): String? = dataStoreHelper.readEncryptedPrefs(REFRESH_TOKEN).firstOrNull()

    suspend fun getUsername(): String? = dataStoreHelper.readPrefs(USERNAME).firstOrNull()

    suspend fun getRole(): String? = dataStoreHelper.readPrefs(ROLE).firstOrNull()

    suspend fun clearToken(){
        dataStoreHelper.clearValue(USERNAME, ROLE)
        dataStoreHelper.clearEncryptedValue(ACCESS_TOKEN, REFRESH_TOKEN)
    }

    companion object{
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USERNAME = stringPreferencesKey("username")
        private val ROLE = stringPreferencesKey("role")
    }

}