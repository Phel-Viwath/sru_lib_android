/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.Manifest
import android.animation.ObjectAnimator
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
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentBookTabBinding
import com.viwath.srulibrarymobile.domain.model.Book
import com.viwath.srulibrarymobile.domain.model.BookId
import com.viwath.srulibrarymobile.domain.model.College
import com.viwath.srulibrarymobile.domain.model.Language
import com.viwath.srulibrarymobile.presentation.event.BookTabEvent
import com.viwath.srulibrarymobile.presentation.event.ResultEvent
import com.viwath.srulibrarymobile.presentation.state.book_state.UploadState
import com.viwath.srulibrarymobile.presentation.ui.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.ui.adapter.BookRecyclerViewAdapter
import com.viwath.srulibrarymobile.presentation.ui.modal.ModalAddBook
import com.viwath.srulibrarymobile.presentation.ui.modal.ModalBorrow
import com.viwath.srulibrarymobile.presentation.viewmodel.BookTabViewModel
import com.viwath.srulibrarymobile.utils.PermissionRequest
import com.viwath.srulibrarymobile.utils.uriToFile
import kotlinx.coroutines.launch

/**
 * This Fragment displays a list of books and provides functionalities to add, update, and delete books.
 * It interacts with a [BookTabViewModel] to manage the book data and handle events.
 *
 * The Fragment also handles file selection for book uploads, storage permissions, and
 * displays UI elements such as a */
@Suppress("DEPRECATION")
class BookTabFragment : Fragment() {

    private var _binding: FragmentBookTabBinding? = null
    private val binding get() = _binding!!

    // dialog
    private var dialogAddUpdate: Dialog? = null
    private var progressDialog: Dialog? = null
    private var dialogBorrow: Dialog? = null

    private lateinit var modalBorrow: ModalBorrow
    private lateinit var modalAddBook: ModalAddBook

    private lateinit var loading: Loading
    private lateinit var permission: PermissionRequest
    private var fileUri: Uri? = null

    private val viewModel: BookTabViewModel by activityViewModels()

    private val _languages: MutableList<Language> = mutableListOf()
    private val _colleges: MutableList<College> = mutableListOf()

    /**
     * [storagePermissionLauncher] is a [androidx.activity.result.ActivityResultLauncher] used to request the read storage permission.
     *
     * When the permission is granted, it launches the [contract] to pick a file with any type.
     *
     * If the permission is denied, it shows a [Snackbar] indicating that the permission is required.
     */
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contract.launch("*/*")
        } else {
            Snackbar.make(binding.root, "Read storage permission is required!", Snackbar.LENGTH_LONG).show()
        }
    }
    /**
     * A contract for launching an activity to get content (a file) from the user's device.
     *
     * This is a registered activity result contract that handles the selection of a file from
     * the device's storage. It uses [ActivityResultContracts.GetContent] to allow the user to
     * choose a file. When a file is selected, the contract's result contains the URI of the
     * selected file. If a URI is successfully obtained, it retrieves the file path and displays
     * an alert dialog to inform the user. If no URI is obtained or the file name cannot be
     * retrieved from the URI, appropriate error messages are displayed via toasts and logs.
     *
     * The lambda function passed to `registerForActivityResult` will be invoked with the
     * selected file's URI as the parameter.
     *
     * @see ActivityResultContracts.GetContent
     * @see registerForActivityResult
     */
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.edtSearch.isFocused){
                        binding.edtSearch.clearFocus()
                    }
                    else{
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            }
        )
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        loading = Loading(requireActivity())
        permission = PermissionRequest(this)
        setupUI(isDarkMode)
        observeViewModel(isDarkMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialogAddUpdate?.dismiss()
        dialogBorrow?.dismiss()
        dialogBorrow = null
        dialogAddUpdate = null
    }

    /**
     * Retrieves the file name from a given content URI.
     *
     * This function queries the content resolver using the provided URI to extract the file's display name.
     *
     * @param context The application context.
     * @param uri The content URI of the file.
     * @return The file name (display name) associated with the URI, or null if the file name cannot be determined.
     */
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

    /**
     * Sets up the UI elements of the view.
     *
     * This function configures the behavior of various UI components, including:
     * - Search EditText: Listens for focus changes to hide/show the bottom navigation.
     * - Root Layout: Listens for touch events to clear focus from the search bar and hide the keyboard.
     * - Floating Action Button (FAB): Sets a click listener to show the add/update book modal.
     * - Refresh Image: Sets the background resource based on dark mode and sets a click listener to refresh data.
     *
     * @param isDarkMode Boolean indicating whether dark mode is enabled. This is used to change the color of refresh icon.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI(isDarkMode: Boolean) {
        binding.edtSearch.setOnFocusChangeListener { _, hasFocus ->
            (requireActivity() as? MainActivity)?.apply {
                if (hasFocus) hideBottomNav() else showBottomNav()
            }
        }
        binding.rootLayout.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
        binding.fabAddBook.setOnClickListener { showAddUpdateBookModal() }
        binding.imgRefresh.apply {
            setBackgroundResource(if (isDarkMode) R.drawable.ic_refresh_light_24 else R.drawable.ic_refresh_night_24)
        }
        binding.imgRefresh.setOnClickListener{
            lifecycleScope.launch {
                if (binding.edtSearch.isFocused)
                    binding.edtSearch.clearFocus()
                hideKeyboard()
                viewModel.loadInitData()
            }
            val animator = ObjectAnimator.ofFloat(binding.imgRefresh, View.ROTATION, 0f, 360f)
            animator.duration = 1000
            animator.start()
        }

    }
    /**
     * Observes the ViewModel's state, events, upload state, and student search results.
     *
     * This function sets up observers for various LiveData streams exposed by the ViewModel,
     * handling UI updates and interactions based on the received data.
     *
     * @param isDarkMode A boolean indicating whether the application is in dark mode. This is used to potentially adjust UI elements.
     *//// viewModel state and event
    private fun observeViewModel(isDarkMode: Boolean) {

        // handle load book
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

        // handle event message
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is ResultEvent.ShowSuccess -> showToast(event.message) // Show success message
                    is ResultEvent.ShowError -> showToast(event.errorMsg) // Show error message
                }
            }
        }

        // handle upload book file
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

        // handle student search
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.findStudentChannel.collect { isFound ->
                modalBorrow.btBorrow.isEnabled = isFound
            }
        }

    }
    private fun setupRecyclerView(books: List<Book>, isDarkMode: Boolean) {
        binding.recyclerBook.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBook.adapter = BookRecyclerViewAdapter(requireContext() ,books, isDarkMode){ book, action ->
           when(action){
               "update" -> showAddUpdateBookModal(book)
               "delete" -> dialogDelete(book.bookId, isDarkMode)
               "borrow" -> dialogBorrow(book)
           }
        }
    }

    private fun showAddUpdateBookModal(book: Book? = null) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.modal_add_book, null)
        val btUploadFile = dialogView.findViewById<MaterialButton>(R.id.btUploadFile)
        if (book != null){
            btUploadFile.isVisible = false
        }
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()
        this.dialogAddUpdate = dialog

        dialog.setOnShowListener{
            dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.material_dialog_background))
            (requireActivity() as? MainActivity)?.hideBottomNav()
        }
        dialog.setOnDismissListener{
            (requireActivity() as? MainActivity)?.showBottomNav()
        }

        modalAddBook = ModalAddBook(dialogView)
        modalAddBook.setupSpinners(requireContext(), _languages, _colleges) // Initialize dropdowns
        book?.let {
            modalAddBook.populateBookData(it)
        }
        dialogView.findViewById<MaterialButton>(R.id.btAddBook).setOnClickListener {
            if (fileUri != null){
                val newFile = fileUri?.uriToFile(requireContext()) ?: return@setOnClickListener
                viewModel.onEvent(BookTabEvent.FileChange(newFile))
                viewModel.onEvent(BookTabEvent.UploadBook)
                return@setOnClickListener
            }
            val (bookId, title, author, genre, year, quan) = modalAddBook.getBookData()
            val (collegeId, languageId) = modalAddBook.getSelectedIds()

            if (bookId.isBlank() || title.isBlank() || collegeId.isBlank() || quan <= 0 || genre.isBlank()) {
                showToast("Please fill in all required fields.")
                return@setOnClickListener
            }

            if (book == null) {
                // Add new book
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
            } else {
                // Update existing book
                viewModel.apply {
                    onEvent(BookTabEvent.BookIdChange(bookId))
                    onEvent(BookTabEvent.BookTittleChange(title))
                    onEvent(BookTabEvent.AuthorChange(author))
                    onEvent(BookTabEvent.GenreChange(genre))
                    onEvent(BookTabEvent.PublicYearChange(year))
                    onEvent(BookTabEvent.QuanChange(quan))
                    onEvent(BookTabEvent.CollegeChange(collegeId))
                    onEvent(BookTabEvent.LanguageChange(languageId))
                    onEvent(BookTabEvent.UpdateBook)
                }
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

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun dialogDelete(bookId: BookId, isDarkMode: Boolean){
        viewModel.onEvent(BookTabEvent.BookIdChange(bookId))
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this book?")
            .setCancelable(true)
            .setPositiveButton("Delete"){d, _ ->
                viewModel.onEvent(BookTabEvent.Remove)
            }
            .setNegativeButton("Cancel"){d, _ ->
                d.dismiss()
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.red))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(
                if (isDarkMode) resources.getColor(R.color.light_color)
                else resources.getColor(R.color.black)
            )
    }

    private fun dialogBorrow(book: Book){
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.modal_borrow, null)
        val dialogBorrow = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setCancelable(true)
            .create()
        this.dialogBorrow = dialogBorrow
        dialogBorrow.setOnShowListener{
            dialogBorrow.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.material_dialog_background))
            (requireActivity() as? MainActivity)?.hideBottomNav()
        }
        dialogBorrow.setOnDismissListener{
            (requireActivity() as? MainActivity)?.showBottomNav()
        }
        modalBorrow = ModalBorrow(view)
        modalBorrow.populateBookData(book)
        modalBorrow.btBorrow.isEnabled = false

        modalBorrow.onSearchClick { studentId ->
            viewModel.apply {
                onEvent(BookTabEvent.StudentIdChange(studentId))
                onEvent(BookTabEvent.GetStudent)
            }
        }
        modalBorrow.onBorrowClick { studentId, quan , bookId->
            viewModel.apply {
                onEvent(BookTabEvent.StudentIdChange(studentId))
                onEvent(BookTabEvent.QuanChange(quan))
                onEvent(BookTabEvent.BookIdChange(bookId))
                onEvent(BookTabEvent.Borrow)
                dialogBorrow.dismiss()
            }
        }
        dialogBorrow.show()
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
    private fun startLoading(): Unit = loading.loadingStart() // Start loading animation
    private fun stopLoading(): Unit = loading.loadingDismiss() // Stop loading animation
    private fun showToast(message: String): Unit = requireActivity().runOnUiThread{
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.edtSearch.clearFocus()
    }
    private fun formatFileSize(size: Long): String = when {
        size >= 1_000_000 -> "%.1f MB".format(size / 1_000_000.0)
        size >= 1_000 -> "%.1f KB".format(size / 1_000.0)
        else -> "$size B"
    }


}
