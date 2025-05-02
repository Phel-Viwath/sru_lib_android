/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShareMainActivityViewModel @Inject constructor() : ViewModel(){

    private val _shouldShowBottomNav = MutableLiveData<Boolean>(true)
    val shouldShowBottomNav: LiveData<Boolean> get() = _shouldShowBottomNav

    fun hideBottomNav(){
        _shouldShowBottomNav.value = false
    }

    fun showBottomNav(){
        _shouldShowBottomNav.value = true
    }

}