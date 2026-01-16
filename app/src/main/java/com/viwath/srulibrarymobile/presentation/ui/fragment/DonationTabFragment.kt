/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentDonationTabBinding
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.Donation
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.presentation.event.DonationTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.ui.adapter.DonationRecycleViewAdapter
import com.viwath.srulibrarymobile.presentation.ui.dialog.DialogAddDonation
import com.viwath.srulibrarymobile.presentation.viewmodel.CollegeViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.DonationTabViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.LanguageViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.CLASSIC
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.MODERN
import com.viwath.srulibrarymobile.presentation.viewmodel.ShareMainActivityViewModel
import com.viwath.srulibrarymobile.utils.getFileNameFromUri
import com.viwath.srulibrarymobile.utils.permission.PermissionRequest
import com.viwath.srulibrarymobile.utils.system.SystemFeature.hideKeyboard
import com.viwath.srulibrarymobile.utils.view_component.showSnackbar
import com.viwath.srulibrarymobile.utils.view_component.showToast
import kotlinx.coroutines.launch

class DonationTabFragment : Fragment(R.layout.fragment_donation_tab) {
    private var _binding: FragmentDonationTabBinding? = null
    private val binding get() = _binding!!

    private var hasLoadData = false

    // dialog
    private var dialog: Dialog? = null

    private val _colleges: MutableList<College> = mutableListOf()
    private val _language: MutableList<Language> = mutableListOf()
    private val _genres: MutableList<Genre> = mutableListOf()

    // view model
    private val donationTabViewModel: DonationTabViewModel by activityViewModels()
    private val connectivityViewModel: ConnectivityViewModel by activityViewModels()
    private val collegeViewModel: CollegeViewModel by activityViewModels()
    private val languageViewModel: LanguageViewModel by activityViewModels()
    private val settingViewModel by activityViewModels<SettingViewModel>()
    private val shareMainActivityViewModel by activityViewModels<ShareMainActivityViewModel>()

    private var isClassicMode = true

    private lateinit var donationRecycleViewAdapter: DonationRecycleViewAdapter
    private lateinit var loading: Loading

    // permission
    private lateinit var permission: PermissionRequest
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contract.launch("*/*")
        } else {
            Snackbar.make(binding.root, "Read storage permission is required!", Snackbar.LENGTH_LONG).show()
        }
    }

    // contract
    private var fileUri: Uri? = null
    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if (uri != null){
            fileUri = uri
            val fileName = uri.getFileNameFromUri(requireContext())
            if (fileName != null){
                showSnackbar(binding.root, "File selected: $fileName.")
            }
            else{
                requireContext().showToast("Unable to retrieve the file name.")
                }
        }
        else requireContext().showToast("No file selected.")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDonationTabBinding.bind(view)

        Log.d("DonationTabFragment", "onViewCreated: DonationTabFragment is created.")

        loading = Loading(requireActivity())
        permission = PermissionRequest(this)
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES


        donationRecycleViewAdapter = setUpRecyclerView(emptyList(), isDarkMode, isClassicMode)
        binding.recyclerBookDonation.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = donationRecycleViewAdapter
        }

        settingViewModel.viewMode.observe(viewLifecycleOwner){ mode ->
            isClassicMode = when(mode){
                CLASSIC -> true
                MODERN -> false
                else -> true
            }
            if (::donationRecycleViewAdapter.isInitialized)
                donationRecycleViewAdapter.updateViewMode(isClassicMode)

        }

        connectivityViewModel.networkStatus.observe(viewLifecycleOwner){ isConnected ->
            if (isConnected)
                observeViewModel()
        }
        viewEvent()
    }

    override fun onResume() {
        super.onResume()
        if (!hasLoadData){
            donationTabViewModel.loadInitData()
            hasLoadData = true
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            Log.d("YourFragment", "Fragment is visible to user")
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        dialog?.dismiss()
        _binding = null
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun viewEvent(){

        binding.swipeRefreshDonation.apply {
            setOnRefreshListener {
                binding.spinnerFilter.setSelection(0)
                if (binding.edtSearchDonation.isFocused)
                    binding.edtSearchDonation.clearFocus()
                requireActivity().hideKeyboard(binding.root)
                lifecycleScope.launch {
                    donationTabViewModel.onEvent(DonationTabEvent.OnReloadList)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    isRefreshing = false
                }, 2000)
            }

        }

        binding.edtSearchDonation.setOnFocusChangeListener {_ , hasFocus ->
            if (hasFocus)
                shareMainActivityViewModel.hideBottomNav()
            else
                shareMainActivityViewModel.showBottomNav()
        }
        binding.rootLayout.setOnTouchListener { _, _ ->
            requireActivity().hideKeyboard(binding.root)
            binding.edtSearchDonation.clearFocus()
            false
        }
        binding.fabAddDonation.setOnClickListener{
            showDonationDialog()
        }

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent?.selectedItem != null){
                    val selectedItem = _genres[position]
                    donationTabViewModel.apply {
                        onEvent(DonationTabEvent.OnFilterGenreChange(selectedItem))
                        onEvent(DonationTabEvent.OnFilter)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            donationTabViewModel.state.collect { state ->
                when {
                    state.isLoading -> loading.startLoading()
                    state.donationList.isNotEmpty() -> {
                        loading.stopLoading()
                        donationRecycleViewAdapter.updateDonationList(state.donationList)
                    }
                    else -> {
                        loading.stopLoading()
                        donationRecycleViewAdapter.updateDonationList(emptyList())
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            donationTabViewModel.resultEvent.collect { result ->
                when (result) {
                    is ResultEvent.ShowError -> showSnackbar(binding.root, result.errorMsg)
                    is ResultEvent.ShowSuccess -> showSnackbar(binding.root, result.message)
                }
            }
        }

        lifecycleScope.launch {
            languageViewModel.state.collect {
                when {
                    it.isLoading -> loading.startLoading()
                    it.error.isNotEmpty() -> {
                        loading.stopLoading()
                        requireContext().showToast(it.error)
                    }
                    else -> {
                        _language.clear()
                        _language.addAll(it.languages)
                        loading.stopLoading()
                    }
                }
            }
        }

        lifecycleScope.launch {
            collegeViewModel.state.collect {
                when {
                    it.isLoading -> loading.startLoading()
                    it.error.isNotEmpty() -> {
                        loading.stopLoading()
                        requireContext().showToast(it.error)
                    }
                    else -> {
                        loading.stopLoading()
                        _colleges.clear()
                        _colleges.addAll(it.colleges)
                    }
                }
            }
        }

        lifecycleScope.launch {
            donationTabViewModel.genreList.collect {
                if (it.isNotEmpty()) {
                    _genres.clear()
                    _genres.addAll(it)
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        it
                    ).also { adapter ->
                        binding.spinnerFilter.adapter = adapter
                    }
                }
            }
        }
    }


    private fun setUpRecyclerView(donationList: List<Donation>, isDarkMode: Boolean, isClassicMode: Boolean): DonationRecycleViewAdapter{
        val adapter = DonationRecycleViewAdapter(requireActivity(), isClassicMode, donationList, isDarkMode){ donation: Donation ->
            showDonationDialog(donation)
        }
        return adapter
    }

    private fun showDonationDialog(initDonation: Donation? = null){
        val view = layoutInflater.inflate(R.layout.dialog_donation, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setCancelable(true)
            .create()
            .apply {
                setOnShowListener{
                    window?.setBackgroundDrawableResource(R.drawable.light_dialog_background)
                }
            }
        dialog.setOnShowListener{
            dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.material_dialog_background))
            shareMainActivityViewModel.hideBottomNav()
        }
        dialog.setOnDismissListener{
            shareMainActivityViewModel.showBottomNav()
        }
        this.dialog = dialog

        DialogAddDonation(view).apply {
            setupSpinners(requireContext(), _language, _colleges)

            initDonation?.let {
                populateData(initDonation)
            }

            // change visibility when user update or add
            onButtonChange {
                it.isVisible = initDonation == null
            }
            // button event in dialog
            onButtonClick(
                onSubmitClick = { donation ->
                    if (initDonation != null){
                        donationTabViewModel.apply {
                            onEvent(DonationTabEvent.OnDonatorIdChange(donation.donatorId!!))
                            onEvent(DonationTabEvent.OnDonatorNameChange(donation.donatorName))
                            onEvent(DonationTabEvent.OnBookIdChange(donation.bookId))
                            onEvent(DonationTabEvent.OnBookTitleChange(donation.bookTitle))
                            onEvent(DonationTabEvent.OnCollegeIdChange(donation.collegeId))
                            onEvent(DonationTabEvent.OnLanguageIdChange(donation.languageId))
                            onEvent(DonationTabEvent.OnAuthorChange(donation.author))
                            onEvent(DonationTabEvent.OnPublicationYearChange(donation.publicationYear))
                            onEvent(DonationTabEvent.OnGenreChange(donation.genre))
                            onEvent(DonationTabEvent.OnDonateDateChange(donation.donateDate))
                            onEvent(DonationTabEvent.OnUpdate)
                        }
                    }
                    else{
                        donationTabViewModel.apply {
                            onEvent(DonationTabEvent.OnDonatorNameChange(donation.donatorName))
                            onEvent(DonationTabEvent.OnBookIdChange(donation.bookId))
                            onEvent(DonationTabEvent.OnBookTitleChange(donation.bookTitle))
                            onEvent(DonationTabEvent.OnCollegeIdChange(donation.collegeId))
                            onEvent(DonationTabEvent.OnLanguageIdChange(donation.languageId))
                            onEvent(DonationTabEvent.OnAuthorChange(donation.author))
                            onEvent(DonationTabEvent.OnPublicationYearChange(donation.publicationYear))
                            onEvent(DonationTabEvent.OnGenreChange(donation.genre))
                            onEvent(DonationTabEvent.OnDonateDateChange(donation.donateDate))
                            onEvent(DonationTabEvent.OnSubmit)
                        }
                    }

                },
                onUploadFileClick = {
                    if (permission.hasReadStoragePermission())
                        contract.launch("*/*")
                    else
                        checkAndRequestStoragePermission()
                }
            )
        }

        dialog.show()

    }

    private fun checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            // For Android 12 and below
            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }


}