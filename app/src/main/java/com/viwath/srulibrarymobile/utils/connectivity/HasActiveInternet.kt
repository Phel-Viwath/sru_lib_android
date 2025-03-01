/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.connectivity

import okio.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Object responsible for checking if there is an active internet connection.
 */
object HasActiveInternet {
    fun execute(): Boolean {
        return try {
            val url = URL("https://www.google.com")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 1500
            connection.readTimeout = 1500
            connection.responseCode == HttpURLConnection.HTTP_OK
        }catch (e: IOException){
            false
        }
    }
}