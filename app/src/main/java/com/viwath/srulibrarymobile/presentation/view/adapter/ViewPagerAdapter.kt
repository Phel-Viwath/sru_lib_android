/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * A custom [FragmentStateAdapter] for populating a [ViewPager2] with a list of Fragments.
 *
 * This adapter takes a list of Fragments and a [FragmentActivity] as input. It then manages the
 * lifecycle and display of these Fragments within the ViewPager2. Each fragment in the provided list
 * will correspond to a page in the ViewPager2.
 *
 * @property fragments The list of [Fragment] instances that will be displayed in the ViewPager2.
 * @property fragmentActivity The [FragmentActivity] that is hosting the ViewPager2 and these fragments.
 *                           It is used as the lifecycle owner for the fragments.
 * @constructor Creates a new instance of [ViewPagerAdapter].
 *
 * Example Usage:
 * ```
 * val fragmentList = listOf(FragmentA(), FragmentB(), FragmentC())
 * val viewPagerAdapter = ViewPagerAdapter(fragmentList, this) // 'this' refers to the hosting FragmentActivity
 * viewPager2.adapter = viewPagerAdapter
 * ```
 */
class ViewPagerAdapter(
    private val fragments: List<Fragment>,
    fragmentActivity: FragmentActivity
): FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}