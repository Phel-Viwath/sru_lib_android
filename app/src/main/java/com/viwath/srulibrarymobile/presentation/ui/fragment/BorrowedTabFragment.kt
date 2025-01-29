/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentBorrowedTabBinding
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow
import com.viwath.srulibrarymobile.presentation.event.BorrowedTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.ui.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.ui.adapter.BorrowRecyclerAdapter
import com.viwath.srulibrarymobile.presentation.viewmodel.BorrowTabViewModal
import kotlinx.coroutines.launch

class BorrowedTabFragment: Fragment(R.layout.fragment_borrowed_tab) {
    private var _binding: FragmentBorrowedTabBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BorrowTabViewModal by activityViewModels()
    private lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBorrowedTabBinding.bind(view)
        mainActivity = (requireActivity() as MainActivity)
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        observerViewModel(isDarkMode)
        setUpUi(isDarkMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun observerViewModel(isDarkMode: Boolean){
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.state.collect{state ->
                when{
                    state.isLoading -> {
                        mainActivity.startLoading()
                    }
                    state.borrowList.isNotEmpty() -> {
                        mainActivity.stopLoading()
                        setUpRecyclerView(state.borrowList, isDarkMode)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.resultEvent.collect { event ->
                when (event) {
                    is ResultEvent.ShowSuccess -> {
                        mainActivity.showSnackBar(event.message)
                    }
                    is ResultEvent.ShowError -> {
                        mainActivity.showToast(event.errorMsg)
                    }
                }
            }
        }

    }

    private fun setUpUi(isDarkMode: Boolean){
        binding.imgRefresh.apply {
            setImageResource(if (isDarkMode) R.drawable.ic_refresh_light_24 else R.drawable.ic_refresh_night_24)
            setOnClickListener{
                lifecycleScope.launch {
                    if (binding.edtSearch.isFocused)
                        binding.edtSearch.clearFocus()
                    mainActivity.hideKeyboard()
                    viewModel.loadInitData()
                }
                val animator = ObjectAnimator.ofFloat(binding.imgRefresh, View.ROTATION, 0f, 360f)
                animator.duration = 1000
                animator.start()
            }
        }
        binding.checkboxFilterBorrow.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.onEvent(BorrowedTabEvent.OnFilterChange(true))
                    viewModel.onEvent(BorrowedTabEvent.FilterOverDue)
                }
                else {
                    viewModel.onEvent(BorrowedTabEvent.OnFilterChange(false))
                    viewModel.onEvent(BorrowedTabEvent.FilterOverDue)
                }
            }
        }

    }

    private fun setUpRecyclerView(list: List<Borrow>, isDarkMode: Boolean){
        binding.recyclerBorrow.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BorrowRecyclerAdapter(list.reversed(), isDarkMode, requireContext())
        binding.recyclerBorrow.adapter = adapter
    }


}