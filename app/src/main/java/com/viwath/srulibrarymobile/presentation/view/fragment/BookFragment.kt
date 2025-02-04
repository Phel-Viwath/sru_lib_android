/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentBookBinding
import com.viwath.srulibrarymobile.presentation.view.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.view.adapter.ViewPagerAdapter
import com.viwath.srulibrarymobile.presentation.viewmodel.BookFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookFragment : Fragment(R.layout.fragment_book){

    private var _binding: FragmentBookBinding?= null
    private val binding get() = _binding!!
    private lateinit var loading: Loading
    private val viewModel: BookFragmentViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookBinding.bind(view)
        loading = Loading(requireActivity())
        mainActivity = (requireActivity() as MainActivity)

        val fragmentList = listOf(
            BookTabFragment(),
            BorrowedTabFragment(),
            DonationFragment(),
            BackupBookFragment()
        )
        val tabTitle = listOf(
            getString(R.string.add_book),
            getString(R.string.borrowed),
            getString(R.string.donation),
            getString(R.string.backup)
        )

//        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
//        val cardList = listOf(binding.cardTotalBook, binding.cardDonateBook, binding.cardBorrow, binding.cardExp, binding.cardBorrowToday, binding.cardReturn)

        binding.viewPagerBook.adapter = ViewPagerAdapter(fragmentList, requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.viewPagerBook){tab, position ->
            tab.text = tabTitle[position]
        }.attach()
        // bind data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect{ state ->
                if (state.isLoading){
                    startLoading()
                }
                if (state.error.isEmpty()){
                    stopLoading()
                    bindData(
                        totalBook = state.totalBook,
                        totalDonation = state.totalDonation,
                        totalBorrowed = state.totalBorrowed,
                        totalExpiration = state.totalExpiration,
                        borrowToday = state.borrowToday,
                        returnToday = state.returnToday
                    )
                }
                if (state.error.isNotEmpty()){
                    stopLoading()
                    Snackbar.make(view, state.error, Snackbar.LENGTH_LONG).show()
                }
            }
        }

    }
    private fun bindData(totalBook: Int, totalDonation: Int, totalBorrowed: Int, totalExpiration: Int, borrowToday: Int, returnToday: Int){
        binding.tvTotalBook.text = "$totalBook"
        binding.tvTotalDonate.text = "$totalDonation"
        binding.tvBorrows.text = "$totalBorrowed"
        binding.tvBorrowExp.text = "$totalExpiration"
        binding.tvBorrowToday.text = "$borrowToday"
        binding.tvReturnToday.text = "$returnToday"
    }


    fun startLoading(){
        mainActivity.startLoading()
    }

    fun stopLoading(){
        mainActivity.stopLoading()
    }

}