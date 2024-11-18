package com.viwath.srulibrarymobile.data.repository

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class TokenManager @Inject constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)

    fun saveAccessToken(accessToken: String){
        prefs.edit().putString(ACCESS_TOKEN, accessToken).apply()
    }

    fun saveRefreshToken(refreshToken: String){
        prefs.edit().putString(REFRESH_TOKEN, refreshToken).apply()
    }

    fun saveUsername(username: String){
        prefs.edit().putString(USERNAME, username).apply()
    }

    fun getAccessToken(): String?{
        return prefs.getString(ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN, null)
    }

    fun getUsername(): String?{
        return prefs.getString(USERNAME, null)
    }

    companion object{
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USERNAME = "username"
    }

}