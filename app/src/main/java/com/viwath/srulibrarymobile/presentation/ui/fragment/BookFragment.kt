/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentBookBinding
import com.viwath.srulibrarymobile.domain.model.book.BookCard
import com.viwath.srulibrarymobile.presentation.ui.adapter.BookCardAdapter
import com.viwath.srulibrarymobile.presentation.ui.adapter.BookPagerAdapter
import com.viwath.srulibrarymobile.presentation.viewmodel.BookFragmentViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.CLASSIC
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.MODERN
import com.viwath.srulibrarymobile.utils.view_component.applyBlur
import com.viwath.srulibrarymobile.utils.view_component.getTranslucentColor
import com.viwath.srulibrarymobile.utils.view_component.showSnackbar
import kotlinx.coroutines.launch

/**
 * The BookFragment displays the main book-related screen in the application.
 *
 * It uses a ViewPager to manage multiple child fragments, each representing a different
 * book-related category (e.g., Add Book, Borrowed, Donation, Backup). It also displays
 * summary statistics about books, donations, borrowed items, etc.
 *
 * @property _binding The binding instance for the fragment's layout. Nullable before onViewCreated.
 * @property binding The non-null binding instance for the fragment's layout.
 * @property viewModel The shared ViewModel for managing book-related data.
 */
class BookFragment : Fragment(){

    private var _binding: FragmentBookBinding?= null
    private val binding get() = _binding!!

    private val viewModel: BookFragmentViewModel by activityViewModels()
    private val settingViewModel by activityViewModels<SettingViewModel>()

    private var bookCardAdapter: BookCardAdapter? = null
    private lateinit var loading: Loading
    private lateinit var bookPagerAdapter: BookPagerAdapter

    private var isClassicMode = true

    val fragmentList by lazy {
        listOf(
            BookTabFragment(),
            BorrowedTabFragment(),
            DonationTabFragment(),
            TrashBookFragment()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookBinding.bind(view)
        loading = Loading(requireActivity())
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // bind data
        setupViewPager()
        observeViewmodel(isDarkMode)

    }

    private fun setupViewPager(){
        bookPagerAdapter = BookPagerAdapter(this, fragmentList)
        val tabTitle by lazy {
            listOf(
                getString(R.string.add_book),
                getString(R.string.borrowed),
                getString(R.string.donation),
                getString(R.string.backup)
            )
        }
        binding.viewPagerBook.offscreenPageLimit = 3
        binding.viewPagerBook.adapter = bookPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerBook){tab, position ->
            tab.text = tabTitle[position]
        }.attach()
    }

    private fun observeViewmodel(isDarkMode: Boolean){

        settingViewModel.viewMode.observe(viewLifecycleOwner) { viewMode ->
            isClassicMode = when(viewMode){
                CLASSIC -> true
                MODERN -> false
                else -> true
            }
            bookCardAdapter?.let { adapter ->
                updateAdapterBackground(adapter, isDarkMode)
            }
            tabLayoutViewMode(isDarkMode, isClassicMode)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect{ state ->
                if (state.isLoading){
                    loading.startLoading()
                }
                if (state.error.isEmpty()){
                    loading.stopLoading()
                    val cardList = listOf(
                        BookCard(resources.getString(R.string.total_book), R.drawable.img_stack_of_books, state.totalBook),
                        BookCard(resources.getString(R.string.total_donate_book), R.drawable.img_books_stack, state.totalDonation),
                        BookCard(resources.getString(R.string.total_borrow), R.drawable.img_reading_book, state.totalBorrowed),
                        BookCard(resources.getString(R.string.total_exp), R.drawable.img_exp_book, state.totalExpiration),
                        BookCard(resources.getString(R.string.borrow_today), R.drawable.img_borrow_book, state.borrowToday),
                        BookCard(resources.getString(R.string.return_today), R.drawable.img_return_book, state.returnToday),
                    )

                    val adapter = BookCardAdapter(requireActivity(), cardList, isClassicMode, isDarkMode)
                    bookCardAdapter = adapter
                    binding.recyclerBookCard.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.recyclerBookCard.adapter = adapter
                }
                if (state.error.isNotEmpty()){
                    loading.stopLoading()
                    showSnackbar(binding.root, state.error)
                }
            }
        }

    }

    private fun updateAdapterBackground(adapter: BookCardAdapter, isDarkMode: Boolean) {
        // Create a new adapter with updated settings and same data
        val updatedAdapter = BookCardAdapter(
            requireActivity(),
            adapter.getData(), // You'll need to add this method to your adapter
            isClassicMode,
            isDarkMode
        )

        // Update the RecyclerView
        binding.recyclerBookCard.adapter = updatedAdapter

        // Update reference
        bookCardAdapter = updatedAdapter
    }

    private fun tabLayoutViewMode(isDarkMode: Boolean, isClassicMode: Boolean){
        if (!isClassicMode){
            binding.bookAppBar.setBackgroundResource(android.R.color.transparent)
            binding.tabLayout.setBackgroundResource(android.R.color.transparent)
            binding.blurViewTabLayout.applyBlur(requireActivity(), 15f, requireContext().getTranslucentColor(isDarkMode))
        }
    }

}