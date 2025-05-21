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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentBackupTabBinding
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.Message
import com.viwath.srulibrarymobile.domain.model.book.BookInTrash
import com.viwath.srulibrarymobile.presentation.event.InTrashEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.ui.adapter.TrashRecyclerViewAdapter
import com.viwath.srulibrarymobile.presentation.viewmodel.TrashTabViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.CLASSIC
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.MODERN
import kotlinx.coroutines.launch

class TrashBookFragment : Fragment(R.layout.fragment_backup_tab) {
    private var _binding: FragmentBackupTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<TrashTabViewModel>()
    private val settingViewModel by activityViewModels<SettingViewModel>()
    private val connectivityViewModel by activityViewModels<ConnectivityViewModel>()

    private var isClassicMode = true

    private lateinit var loading: Loading
    private lateinit var trashRecyclerViewAdapter: TrashRecyclerViewAdapter

    private val _genres = mutableListOf<Genre>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("TrashFragment", "onViewCreated: TrashFragment5 created")

        _binding = FragmentBackupTabBinding.bind(view)
        loading = Loading(requireActivity())

        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        trashRecyclerViewAdapter = setUpRecyclerView(emptyList(), isDarkMode, isClassicMode)
        binding.recyclerViewTrash.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trashRecyclerViewAdapter
        }

        settingViewModel.viewMode.observe(viewLifecycleOwner){ viewMode ->
            isClassicMode = when(viewMode){
                CLASSIC -> true
                MODERN -> false
                else -> true
            }
            if (::trashRecyclerViewAdapter.isInitialized)
                trashRecyclerViewAdapter.updateViewMode(isClassicMode)
        }

        connectivityViewModel.networkStatus.observe(viewLifecycleOwner){ isConnected ->
            if (isConnected)
                observeMainViewModel()
        }

        viewAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewAction(){
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                this.isRefreshing = false
                viewModel.onEvent(InTrashEvent.RefreshData)
                Handler(Looper.getMainLooper()).postDelayed({
                    this@apply.isRefreshing = false
                },2000)
            }
        }

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent?.selectedItem != null){
                    val selectedItem = _genres[position]
                    viewModel.apply {
                        onEvent(InTrashEvent.FilterGenreChange(selectedItem))
                        onEvent(InTrashEvent.Filter)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun observeMainViewModel() {

        lifecycleScope.launch {
            viewModel.state.collect{ state ->
                when{
                    state.isLoading -> loading.startLoading()
                    state.booksInTrash.isNotEmpty() -> {
                        loading.stopLoading()
                        trashRecyclerViewAdapter.updateBookData(state.booksInTrash)
                    }
                    else -> {
                        loading.stopLoading()
                        trashRecyclerViewAdapter.updateBookData(emptyList())
                    }
                }
            }
        }//

        lifecycleScope.launch {
            viewModel.resultEvent.collect {
                when(it){
                    is ResultEvent.ShowSuccess -> showSnackbar(it.message)
                    is ResultEvent.ShowError -> showSnackbar(it.errorMsg)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.genres.collect { g ->
                if (g.isNotEmpty()){
                    _genres.clear()
                    _genres.addAll(g)
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, g).also{
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerFilter.adapter = it
                    }
                }
            }
        }


    }

    private fun setUpRecyclerView(bookInTrash: List<BookInTrash>, isDarkMode: Boolean, isClassicMode: Boolean): TrashRecyclerViewAdapter {
        val adapter = TrashRecyclerViewAdapter(isClassicMode, bookInTrash,requireActivity(), isDarkMode){
            alertDialog(it.bookId)
        }
        return adapter
    }

    private fun showSnackbar(message: Message){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun alertDialog(bookId: BookId){
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Application")
            .setMessage("Do you want to restore or delete this book?")
            .setPositiveButton("Restore") { _, _ ->
                viewModel.onEvent(InTrashEvent.OnRestoreClicked(bookId))
                viewModel.onEvent(InTrashEvent.RestoreBook)
            }
            .setNegativeButton("Delete") { _, _ ->
                viewModel.onEvent(InTrashEvent.OnDeleteClicked(bookId))
                viewModel.onEvent(InTrashEvent.DeleteBook)
            }
            .show()
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(requireContext(),R.color.red))
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(requireContext(),R.color.green))
    }


}
