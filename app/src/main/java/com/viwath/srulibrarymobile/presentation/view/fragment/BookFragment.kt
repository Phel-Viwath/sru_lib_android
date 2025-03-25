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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentBookBinding
import com.viwath.srulibrarymobile.domain.model.book.BookCard
import com.viwath.srulibrarymobile.presentation.view.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.view.adapter.BookCardAdapter
import com.viwath.srulibrarymobile.presentation.view.adapter.ViewPagerAdapter
import com.viwath.srulibrarymobile.presentation.viewmodel.BookFragmentViewModel
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
 * @property loading A Loading instance for showing/hiding loading indicators.
 * @property viewModel The shared ViewModel for managing book-related data.
 * @property mainActivity The parent MainActivity instance.
 */
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
            DonationTabFragment(),
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
                    mainActivity.startLoading()
                }
                if (state.error.isEmpty()){
                    mainActivity.stopLoading()
                    val cardList = listOf(
                        BookCard(resources.getString(R.string.total_book), R.drawable.img_stack_of_books, state.totalBook),
                        BookCard(resources.getString(R.string.total_donate_book), R.drawable.img_books_stack, state.totalDonation),
                        BookCard(resources.getString(R.string.total_borrow), R.drawable.img_reading_book, state.totalBorrowed),
                        BookCard(resources.getString(R.string.total_exp), R.drawable.img_exp_book, state.totalExpiration),
                        BookCard(resources.getString(R.string.borrow_today), R.drawable.img_borrow_book, state.borrowToday),
                        BookCard(resources.getString(R.string.return_today), R.drawable.img_return_book, state.returnToday),
                    )

                    val adapter = BookCardAdapter(cardList)
                    binding.recyclerBookCard.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.recyclerBookCard.adapter = adapter
                }
                if (state.error.isNotEmpty()){
                    mainActivity.stopLoading()
                    Snackbar.make(view, state.error, Snackbar.LENGTH_LONG).show()
                }
            }
        }


    }

}