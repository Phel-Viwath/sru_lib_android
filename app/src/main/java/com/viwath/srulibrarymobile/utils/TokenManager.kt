/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils

import android.content.Context

class TokenManager(context: Context) {

    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context)

    fun saveAccessToken(accessToken: String){
        sharedPreferencesHelper.putString(ACCESS_TOKEN, accessToken)
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

    fun getAccessToken(): String?{
        return sharedPreferencesHelper.getString(ACCESS_TOKEN)
    }

    fun getRefreshToken(): String? = sharedPreferencesHelper.getEncryptedString(REFRESH_TOKEN)

    fun getUsername(): String? = sharedPreferencesHelper.getString(USERNAME)

    fun getRole(): String? = sharedPreferencesHelper.getString(ROLE)

    fun clearToken(){
        sharedPreferencesHelper.clearValue(ACCESS_TOKEN, USERNAME, ROLE)
        sharedPreferencesHelper.clearValue(REFRESH_TOKEN)
    }

    companion object{

        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USERNAME = "username"
        private const val ROLE = "role"
    }

}