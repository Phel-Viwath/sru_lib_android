/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.data.dto.BookDto
import com.viwath.srulibrarymobile.databinding.FragmentBookTabBinding
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.presentation.event.BookTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.book_state.UploadState
import com.viwath.srulibrarymobile.presentation.ui.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.ui.adapter.BookRecyclerViewAdapter
import com.viwath.srulibrarymobile.presentation.ui.modal.ModalAddBook
import com.viwath.srulibrarymobile.presentation.viewmodel.BookTabViewModel
import com.viwath.srulibrarymobile.utils.PermissionRequest
import com.viwath.srulibrarymobile.utils.uriToFile
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class BookTabFragment : Fragment() {

    private var _binding: FragmentBookTabBinding? = null
    private val binding get() = _binding!!
    private var dialog: Dialog? = null
    private var progressDialog: Dialog? = null

    private lateinit var loading: Loading
    private lateinit var permission: PermissionRequest
    private var fileUri: Uri? = null

    private val viewModel: BookTabViewModel by activityViewModels()

    private val _languages: MutableList<Language> = mutableListOf()
    private val _colleges: MutableList<College> = mutableListOf()

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contract.launch("*/*")
        } else {
            Snackbar.make(binding.root, "Read storage permission is required!", Snackbar.LENGTH_LONG).show()
        }
    }
    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if (uri != null) {
            fileUri = uri
            val filePath = getFileNameFromUri(requireContext(), uri)
            if (filePath != null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Message")
                    .setMessage("File selected: $filePath. Click button submit to upload.")
                    .setCancelable(true)
                    .create()
                    .show()
                Log.d("BookTabFragment", "File selected: $filePath")
            } else {
                showToast("Unable to retrieve the file name.")
                Log.d("BookTabFragment", "Unable to retrieve the file name.")
            }
        } else {
            showToast("No file selected.")
            Log.d("BookTabFragment", "No file selected.")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        loading = Loading(requireActivity())
        permission = PermissionRequest(this)
        setupUI()
        observeViewModel(isDarkMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialog?.dismiss()
        dialog = null
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    it.getString(displayNameIndex)
                } else null
            } else null
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        binding.edtSearch.setOnFocusChangeListener { _, hasFocus ->
            (requireActivity() as? MainActivity)?.apply {
                if (hasFocus) hideBottomNav() else showBottomNav()
            }
        }

        binding.rootLayout.setOnTouchListener { _, _ ->
            binding.edtSearch.clearFocus()
            hideKeyboard()
            false
        }

        binding.fabAddBook.setOnClickListener { showAddBookModal() }
    }
    /// viewModel state and event
    private fun observeViewModel(isDarkMode: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when {
                    state.isLoading -> startLoading() // Show loading indicator
                    state.error.isNotBlank() -> {
                        stopLoading()
                        showToast(state.error) // Display error message
                    }
                    state.books.isNotEmpty() -> {
                        stopLoading()
                        setupRecyclerView(state.books, isDarkMode) // Display list of books

                        _languages.clear()
                        _languages.addAll(state.language)
                        _colleges.clear()
                        _colleges.addAll(state.college)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is ResultEvent.ShowSuccess -> showToast(event.message) // Show success message
                    is ResultEvent.ShowError -> showToast(event.errorMsg) // Show error message
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uploadState.collect { state ->
                when (state) {
                    is UploadState.Idle -> {
                        // Initial state, do nothing
                    }
                    is UploadState.Uploading -> {
                        showProgressDialog(state.progress, state.uploadedSize, state.totalSize)
                    }
                    is UploadState.Success -> {
                        dismissProgressDialog()
                        showToast("File uploaded successfully")
                    }
                    is UploadState.Error -> {
                        dismissProgressDialog()
                        showToast(state.message)
                    }
                }
            }
        }

    }
    private fun setupRecyclerView(books: List<BookDto>, isDarkMode: Boolean) {
        binding.recyclerBook.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBook.adapter = BookRecyclerViewAdapter(books, isDarkMode){
            showToast(it.bookTitle)
        }
    }

    private fun startLoading(): Unit = loading.loadingStart() // Start loading animation
    private fun stopLoading(): Unit = loading.loadingDismiss() // Stop loading animation
    private fun showToast(message: String): Unit = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun showAddBookModal() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.modal_add_book, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        this.dialog = dialog

        dialog.setOnShowListener{
            dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.material_dialog_background))
            (requireActivity() as? MainActivity)?.hideBottomNav()
        }
        dialog.setOnDismissListener{
            (requireActivity() as? MainActivity)?.showBottomNav()
        }

        val inputs = ModalAddBook(dialogView)
        inputs.setupSpinners(requireContext(), _languages, _colleges) // Initialize dropdowns
        val btUploadFile = dialogView.findViewById<MaterialButton>(R.id.btUploadFile)

        dialogView.findViewById<MaterialButton>(R.id.btAddBook).setOnClickListener {
            if (fileUri != null){
                val newFile = fileUri?.uriToFile(requireContext()) ?: return@setOnClickListener
                viewModel.onEvent(BookTabEvent.FileChange(newFile))
                viewModel.onEvent(BookTabEvent.UploadBook)
                return@setOnClickListener
            }
            val (bookId, title, author, genre, year, quan) = inputs.getBookData()
            val (collegeId, languageId) = inputs.getSelectedIds()

            if (bookId.isBlank() || title.isBlank() || collegeId.isBlank() || quan <= 0 || genre.isBlank()) {
                showToast("Please fill in all required fields.")
                return@setOnClickListener
            }

            viewModel.apply {
                onEvent(BookTabEvent.BookIdChange(bookId))
                onEvent(BookTabEvent.BookTittleChange(title))
                onEvent(BookTabEvent.AuthorChange(author))
                onEvent(BookTabEvent.GenreChange(genre))
                onEvent(BookTabEvent.PublicYearChange(year))
                onEvent(BookTabEvent.QuanChange(quan))
                onEvent(BookTabEvent.CollegeChange(collegeId))
                onEvent(BookTabEvent.LanguageChange(languageId))
                onEvent(BookTabEvent.SaveBook)
            }

            dialog.dismiss()
        }

        btUploadFile.setOnClickListener{
            if (fileUri != null){
                btUploadFile.setBackgroundResource(R.color.light_green)
            }
            // permission
            if (permission.hasReadStoragePermission()) {
                contract.launch("*/*") // Permissions already granted, open file picker
            } else checkAndRequestStoragePermission()
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

    @SuppressLint("SetTextI18n")
    private fun showProgressDialog(progress: Int, uploaded: Long, total: Long) {
        if (progressDialog == null) {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.upload_progress_dialog, null)

            progressDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create()
                .apply {
                    setOnShowListener {
                        window?.setBackgroundDrawableResource(R.drawable.light_dialog_background)
                    }
                }
            progressDialog?.show()
        }
        progressDialog?.findViewById<ProgressBar>(R.id.progressBar)?.progress = progress
        progressDialog?.findViewById<TextView>(R.id.tvProgressPercentage)?.text =
            "${formatFileSize(uploaded)} / ${formatFileSize(total)} ($progress%)"
    }

    private fun formatFileSize(size: Long): String = when {
        size >= 1_000_000 -> "%.1f MB".format(size / 1_000_000.0)
        size >= 1_000 -> "%.1f KB".format(size / 1_000.0)
        else -> "$size B"
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }


}
