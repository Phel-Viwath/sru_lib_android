/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.connectivity

import okio.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Object responsible for checking if there is an active internet connection.
 */
object HasActiveInternet {
    fun execute(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()
                true
            }
        }catch (e: IOException){
            false
        }
    }
}