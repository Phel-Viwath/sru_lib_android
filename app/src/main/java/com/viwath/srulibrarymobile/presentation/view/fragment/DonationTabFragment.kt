/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.viwath.srulibrarymobile.databinding.FragmentDonationTabBinding
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.Donation
import com.viwath.srulibrarymobile.domain.model.Genre
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.presentation.event.DonationTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.view.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.view.adapter.DonationRecycleViewAdapter
import com.viwath.srulibrarymobile.presentation.view.dialog.DialogAddDonation
import com.viwath.srulibrarymobile.presentation.viewmodel.CollegeViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.DonationTabViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.LanguageViewModel
import com.viwath.srulibrarymobile.utils.permission.PermissionRequest
import com.viwath.srulibrarymobile.utils.getFileNameFromUri
import kotlinx.coroutines.launch

class DonationTabFragment : Fragment(R.layout.fragment_donation_tab) {
    private var _binding: FragmentDonationTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    // dialog
    private var dialog: Dialog? = null

    private val _colleges: MutableList<College> = mutableListOf()
    private val _language: MutableList<Language> = mutableListOf()
    private val _genres: MutableList<Genre> = mutableListOf()

    // view model
    private val viewModel: DonationTabViewModel by activityViewModels()
    private val connectivityViewModel: ConnectivityViewModel by activityViewModels()
    private val collegeViewModel: CollegeViewModel by activityViewModels()
    private val languageViewModel: LanguageViewModel by activityViewModels()

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
            val filePath = uri.getFileNameFromUri(requireContext())
            if (filePath != null){
                mainActivity.showSnackbar("File selected: $filePath.")
            }
            else{
                mainActivity.showToast("Unable to retrieve the file name.")
            }
        }
        else mainActivity.showToast("No file selected.")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDonationTabBinding.bind(view)
        mainActivity = (requireActivity() as MainActivity)
        permission = PermissionRequest(this)
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        connectivityViewModel.networkStatus.observe(viewLifecycleOwner){ isConnected ->
            if (isConnected)
                observeViewModel(isDarkMode)
        }
        viewEvent(isDarkMode)

    }
    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun viewEvent(isDarkMode: Boolean){
        binding.edtSearchDonation.setOnFocusChangeListener {_ , hasFocus ->
            mainActivity.apply {
                if (hasFocus) hideBottomNav() else showBottomNav()
            }
        }
        binding.rootLayout.setOnTouchListener { _, _ ->
            mainActivity.hideKeyboard()
            binding.edtSearchDonation.clearFocus()
            false
        }
        binding.fabAddDonation.setOnClickListener{
            showDonationDialog()
        }
        binding.imgRefresh.apply {
            setImageResource(if (isDarkMode) R.drawable.ic_refresh_light_24 else R.drawable.ic_refresh_night_24)
            setOnClickListener{
                binding.spinnerFilter.setSelection(0)
                if (binding.edtSearchDonation.isFocused)
                    binding.edtSearchDonation.clearFocus()
                mainActivity.hideKeyboard()
                lifecycleScope.launch {
                    viewModel.loadInitData()
                }
                val animator = ObjectAnimator.ofFloat(binding.imgRefresh, View.ROTATION, 0f, 360f)
                animator.duration = 1000
                animator.start()
            }
        }

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent?.selectedItem != null){
                    val selectedItem = _genres[position]
                    viewModel.apply {
                        onEvent(DonationTabEvent.OnFilterGenreChange(selectedItem))
                        onEvent(DonationTabEvent.OnFilter)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

    }

    private fun observeViewModel(isDarkMode: Boolean){

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.state.collect{ state ->
                when{
                    state.isLoading -> mainActivity.startLoading()
                    state.donationList.isNotEmpty() -> {
                        mainActivity.stopLoading()
                        Log.d("DonationTabFragment", "observeViewModel: ${state.donationList}")
                        setUpRecyclerView(state.donationList, isDarkMode)
                    }
                    else -> mainActivity.stopLoading()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.resultEvent.collect{ result ->
                when(result){
                    is ResultEvent.ShowError -> mainActivity.showSnackbar(result.errorMsg)
                    is ResultEvent.ShowSuccess -> mainActivity.showSnackbar(result.message)
                }
            }
        }

        lifecycleScope.launch{
            languageViewModel.state.collect{
                when{
                    it.isLoading -> mainActivity.startLoading()
                    it.error.isNotEmpty() -> {
                        mainActivity.stopLoading()
                        mainActivity.showToast(it.error)
                    }
                    else -> {
                        _language.clear()
                        _language.addAll(it.languages)
                        mainActivity.stopLoading()
                    }
                }
            }
        }
        lifecycleScope.launch{
            collegeViewModel.state.collect{
                when{
                    it.isLoading -> mainActivity.startLoading()
                    it.error.isNotEmpty() -> {
                        mainActivity.stopLoading()
                        mainActivity.showToast(it.error)
                    }
                    else -> {
                        mainActivity.stopLoading()
                        _colleges.clear()
                        _colleges.addAll(it.colleges)
                    }
                }
            }
        }

        lifecycleScope.launch{
            viewModel.genreList.collect{
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, it
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerFilter.adapter = adapter
                }
            }
        }

        lifecycleScope.launch{
            viewModel.genreList.collect{
                if (it.isNotEmpty()){
                    _genres.clear()
                    _genres.addAll(it)
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, it).also { adapter ->
                        binding.spinnerFilter.adapter = adapter
                    }
                }
            }
        }

    }

    private fun setUpRecyclerView(donationList: List<Donation>, isDarkMode: Boolean){
        val adapter = DonationRecycleViewAdapter(donationList, isDarkMode){ donation: Donation ->
            showDonationDialog(donation)
        }
        binding.recyclerBookDonation.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
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
            (requireActivity() as? MainActivity)?.hideBottomNav()
        }
        dialog.setOnDismissListener{
            (requireActivity() as? MainActivity)?.showBottomNav()
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
                        viewModel.apply {
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
                        viewModel.apply {
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