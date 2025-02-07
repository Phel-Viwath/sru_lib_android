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
import android.util.Log
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [NetworkConnectivityObserver] is a concrete implementation of [ConnectivityObserver]
 * that observes network connectivity changes and emits a [Boolean] value indicating
 * whether a validated internet connection is available.
 *
 * It utilizes the [ConnectivityManager] to register a network callback and listens for
 * network availability, loss, and capability changes.
 *
 * @property context The application context used to obtain the [ConnectivityManager].
 * @constructor Creates a [NetworkConnectivityObserver] instance.
 *
 * This class uses the hilt library to inject the application context
 *
 */
class NetworkConnectivityObserver @Inject constructor (
    @ApplicationContext context: Context
): ConnectivityObserver {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!

    override fun networkObserve(): Flow<Boolean> {
        return callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(true)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.d("NetworkObserver", "Network lost")
                    trySend(false)
                }
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val hasNetworkCapabilities = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    Log.d("NetworkObserver", "Network onCapabilitiesChanged. Has internet: $")
                    if (hasNetworkCapabilities == true){
                        CoroutineScope(IO).launch{
                            val hasInternet = HasActiveInternet.execute()
                            trySend(hasInternet)
                        }
                    }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(networkCallback)

            awaitClose{
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }

        }.distinctUntilChanged()
    }



}