/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class NetworkConnectivityObserver @Inject constructor (
    @ApplicationContext context: Context
): ConnectivityObserver {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun networkObserve(): Flow<Status> {
        return callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(Status.CONNECTED)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(Status.DISCONNECTED)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val status = when{
                        connectivityManager.activeNetwork == null -> Status.DISCONNECTED
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> Status.CONNECTED_WIFI
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> Status.CONNECTED_MOBILE
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) -> Status.CONNECTED_UNMETERED
                        else -> Status.DISCONNECTED
                    }
                    trySend(status)
                }

            }
            connectivityManager.registerDefaultNetworkCallback(networkCallback)

            awaitClose{
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }

        }.distinctUntilChanged()
    }


}