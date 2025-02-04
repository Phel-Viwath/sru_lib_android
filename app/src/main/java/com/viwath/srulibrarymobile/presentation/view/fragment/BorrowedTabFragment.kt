/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.fragment

import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentBorrowedTabBinding
import com.viwath.srulibrarymobile.domain.model.borrow.Borrow
import com.viwath.srulibrarymobile.presentation.event.BorrowedTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.view.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.view.adapter.BorrowRecyclerAdapter
import com.viwath.srulibrarymobile.presentation.view.dialog.DialogExtendOrReturn
import com.viwath.srulibrarymobile.presentation.viewmodel.BorrowTabViewModel
import kotlinx.coroutines.launch

class BorrowedTabFragment: Fragment(R.layout.fragment_borrowed_tab) {
    private var _binding: FragmentBorrowedTabBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BorrowTabViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity
    private lateinit var dialogExtendOrReturn: DialogExtendOrReturn

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
                    else -> {
                        mainActivity.stopLoading()
                        setUpRecyclerView(emptyList(), isDarkMode)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.resultEvent.collect { event ->
                when (event) {
                    is ResultEvent.ShowSuccess -> {
                        mainActivity.showSnackbar(event.message)
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

    private fun setUpRecyclerView(list: List<Borrow>, isDarkMode: Boolean){
        binding.recyclerBorrow.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BorrowRecyclerAdapter(list.reversed(), isDarkMode, requireContext()){
            showDialogReturnOrExtend(it, isDarkMode)
        }
        binding.recyclerBorrow.adapter = adapter
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