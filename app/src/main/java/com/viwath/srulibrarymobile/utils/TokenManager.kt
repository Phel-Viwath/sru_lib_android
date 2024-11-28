package com.viwath.srulibrarymobile.utils

import android.content.Context

class TokenManager(private val context: Context) {

    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context)

    fun saveAccessToken(accessToken: String){
        sharedPreferencesHelper.putString(ACCESS_TOKEN, accessToken)
    }

    fun saveRefreshToken(refreshToken: String){
        sharedPreferencesHelper.putString(REFRESH_TOKEN, refreshToken)
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

    fun getRefreshToken(): String? = sharedPreferencesHelper.getString(REFRESH_TOKEN)

    fun getUsername(): String? = sharedPreferencesHelper.getString(USERNAME)

    fun getRole(): String? = sharedPreferencesHelper.getString(ROLE)

    fun clearToken(){
        sharedPreferencesHelper.clearValue(ACCESS_TOKEN, REFRESH_TOKEN, USERNAME, ROLE)
    }

    companion object{

        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USERNAME = "username"
        private const val ROLE = "role"
    }

}