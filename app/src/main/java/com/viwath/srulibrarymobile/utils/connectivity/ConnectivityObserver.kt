/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.utils.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver{
    fun networkObserve(): Flow<Boolean>
}