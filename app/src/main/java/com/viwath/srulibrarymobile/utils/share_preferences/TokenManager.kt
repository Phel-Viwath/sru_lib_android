/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.share_preferences

import android.content.Context

/**
 * Manages the storage and retrieval of authentication tokens and user information.
 *
 * This class provides methods to save, retrieve, and clear access tokens, refresh tokens,
 * usernames, and user roles using SharedPreferences. It utilizes the `SharedPreferencesHelper`
 * for interacting with SharedPreferences.
 *
 * @property context The Android application context used to access SharedPreferences.
 */
class TokenManager(context: Context) {

    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context)

    fun saveAccessToken(accessToken: String){
        sharedPreferencesHelper.putEncryptedString(ACCESS_TOKEN, accessToken)
    }

    fun saveRefreshToken(refreshToken: String){
        sharedPreferencesHelper.putEncryptedString(REFRESH_TOKEN, refreshToken)
    }

    fun saveUsername(username: String){
        sharedPreferencesHelper.putString(USERNAME, username)
    }

    fun saveRole(role: String){
        sharedPreferencesHelper.putString(ROLE, role)
    }

    fun getAccessToken(): String? = sharedPreferencesHelper.getEncryptedString(ACCESS_TOKEN)

    fun getRefreshToken(): String? = sharedPreferencesHelper.getEncryptedString(REFRESH_TOKEN)

    fun getUsername(): String? = sharedPreferencesHelper.getString(USERNAME)

    fun getRole(): String? = sharedPreferencesHelper.getString(ROLE)

    fun clearToken(){
        sharedPreferencesHelper.clearValue(USERNAME, ROLE)
        sharedPreferencesHelper.clearEncryptedValue(REFRESH_TOKEN, ACCESS_TOKEN)
    }

    companion object{

        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USERNAME = "username"
        private const val ROLE = "role"
    }

}