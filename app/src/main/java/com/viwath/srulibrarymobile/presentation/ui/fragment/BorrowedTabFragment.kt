/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentBorrowedTabBinding
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow
import com.viwath.srulibrarymobile.presentation.event.BorrowedTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.ui.adapter.BorrowRecyclerAdapter
import com.viwath.srulibrarymobile.presentation.ui.dialog.DialogExtendOrReturn
import com.viwath.srulibrarymobile.presentation.viewmodel.BorrowTabViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.CLASSIC
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.MODERN
import com.viwath.srulibrarymobile.utils.system.SystemFeature.hideKeyboard
import com.viwath.srulibrarymobile.utils.view_component.showSnackbar
import com.viwath.srulibrarymobile.utils.view_component.showToast
import kotlinx.coroutines.launch


/**
 * Fragment responsible for displaying and managing the list of borrowed items.
 *
 * This fragment allows users to view their borrowed items, filter them, search for specific items,
 * and initiate the process of returning or extending a borrowed item's due date.
 *
 * @constructor Creates a [BorrowedTabFragment] instance.
 * @property viewModel The [BorrowTabViewModel] instance used to interact with the data layer.
 * @property connectivityViewModel The [ConnectivityViewModel] instance used to observe network connectivity.
 * @property dialogExtendOrReturn A custom dialog [DialogExtendOrReturn] for returning or extending an item.
 */
class BorrowedTabFragment: Fragment(R.layout.fragment_borrowed_tab) {
    private var _binding: FragmentBorrowedTabBinding? = null
    private val binding get() = _binding!!

    // view model
    private val viewModel: BorrowTabViewModel by activityViewModels()
    private val connectivityViewModel: ConnectivityViewModel by activityViewModels()
    private val settingViewModel by activityViewModels<SettingViewModel>()

    private lateinit var dialogExtendOrReturn: DialogExtendOrReturn
    private lateinit var borrowRecyclerAdapter: BorrowRecyclerAdapter
    private lateinit var loading: Loading

    private var isClassicMode = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BorrowedTabFragment", "onViewCreated: BorrowedTabFragment is created")
        _binding = FragmentBorrowedTabBinding.bind(view)
        loading = Loading(requireActivity())
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        //
        borrowRecyclerAdapter = setUpRecyclerView(emptyList(), isDarkMode, isClassicMode)
        binding.recyclerBorrow.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = borrowRecyclerAdapter
        }

        settingViewModel.viewMode.observe(viewLifecycleOwner) { viewMode ->
            isClassicMode = when(viewMode){
                CLASSIC -> true
                MODERN -> false
                else -> true
            }

            if (::borrowRecyclerAdapter.isInitialized)
                borrowRecyclerAdapter.updateViewMode(isClassicMode)
        }

        connectivityViewModel.networkStatus.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected)
                observerViewModel()
        }

        setUpUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observerViewModel(){
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.state.collect{state ->
                when{
                    state.isLoading -> loading.startLoading()
                    state.borrowList.isNotEmpty() -> {
                        loading.stopLoading()
                        borrowRecyclerAdapter.updateBorrowList(state.borrowList)
                    }
                    else -> {
                       loading.stopLoading()
                       borrowRecyclerAdapter.updateBorrowList(emptyList())
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.resultEvent.collect { event ->
                when (event) {
                    is ResultEvent.ShowSuccess -> {
                        showSnackbar(binding.root,event.message)
                    }
                    is ResultEvent.ShowError -> {
                        requireContext().showToast(event.errorMsg)
                    }
                }
            }
        }

    }

    private fun setUpUi(){

        binding.swipeRefreshBorrowed.apply {
            setOnRefreshListener {
                this.isRefreshing = false
                lifecycleScope.launch {
                    if (binding.edtSearch.isFocused)
                        binding.edtSearch.clearFocus()
                    requireActivity().hideKeyboard(binding.root)
                    viewModel.onEvent(BorrowedTabEvent.LoadBorrowList)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    this@apply.isRefreshing = false
                },2000)
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

        binding.checkboxGetAllBorrow.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                viewModel.onEvent(BorrowedTabEvent.GetAllBorrow)
            }else {
                viewModel.onEvent(BorrowedTabEvent.OnFilterChange(false))
                viewModel.onEvent(BorrowedTabEvent.FilterOverDue)
            }
        }

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(BorrowedTabEvent.OnSearchTextChange(text.toString().trim()))
            viewModel.onEvent(BorrowedTabEvent.FilterByTextSearch)
        }
        binding.tilSearchBorrow.setEndIconOnClickListener{
            val keyword = binding.edtSearch.text.toString().trim()
            viewModel.onEvent(BorrowedTabEvent.OnSearchTextChange(keyword))
            viewModel.onEvent(BorrowedTabEvent.SearchBorrow)
        }

    }

    private fun setUpRecyclerView(
        list: List<Borrow>, isDarkMode:
        Boolean, isClassicMode: Boolean
    ): BorrowRecyclerAdapter = BorrowRecyclerAdapter(
        list.reversed(),
        isClassicMode,
        isDarkMode,
        requireActivity()
    ){
        showDialogReturnOrExtend(it, isDarkMode)
    }



    private fun showDialogReturnOrExtend(borrow: Borrow, isDarkMode: Boolean){
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_extend, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setCancelable(false)
            .create()
        dialogExtendOrReturn = DialogExtendOrReturn(view)
        dialogExtendOrReturn.apply {
            themeChange(isDarkMode, requireContext())
            populateReturnData(borrow)
            onRadioChange { isReturn ->
                dialogExtendOrReturn.setVisibility(isReturn)
            }
            onExitClick {
                dialog.dismiss()
            }
            onReturnOrExtendClick(borrow){ studentId, bookId, borrowId ->
                when {
                    studentId == null && bookId == null && borrowId != null -> {
                        viewModel.onEvent(BorrowedTabEvent.OnBorrowIdChange(borrowId))
                        viewModel.onEvent(BorrowedTabEvent.ExtendBorrow)
                    }
                    studentId != null && bookId != null && borrowId == null -> {
                        viewModel.onEvent(BorrowedTabEvent.OnReturnBookDataChange(studentId, bookId))
                        viewModel.onEvent(BorrowedTabEvent.ReturnBook)
                    }
                    else -> return@onReturnOrExtendClick
                }
            }
        }


        dialog.show()
    }


}