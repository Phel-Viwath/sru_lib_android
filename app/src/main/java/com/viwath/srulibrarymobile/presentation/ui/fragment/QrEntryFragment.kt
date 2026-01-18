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
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.common.Loading
import com.viwath.srulibrarymobile.databinding.FragmentQrBinding
import com.viwath.srulibrarymobile.presentation.event.QrEntryEvent
import com.viwath.srulibrarymobile.presentation.state.QrFragmentState
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.QrFragmentViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.CLASSIC
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.MODERN
import com.viwath.srulibrarymobile.utils.permission.PermissionLauncher.cameraPermissionLauncher
import com.viwath.srulibrarymobile.utils.permission.PermissionRequest
import com.viwath.srulibrarymobile.utils.qr_reader.QrCodeCallback
import com.viwath.srulibrarymobile.utils.view_component.applyBlur
import com.viwath.srulibrarymobile.utils.view_component.setIcon
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * QrEntryFragment is a Fragment responsible for handling QR code scanning and student attendance entry.
 * It allows users to scan a QR code, retrieve student information, select the purpose of the visit,
 * and save the attendance record.
 *
 * This fragment utilizes:
 *   - CameraX for camera preview and QR code scanning.
 *   - ViewModel for managing UI state and business logic.
 *   - Data Binding for connecting UI elements to data.
 *   - Coroutines for handling asynchronous tasks.
 *   - Android's permission system for camera access.
 *   - UI elements for displaying loading indicators, success messages, and error alerts.
 *   - Custom classes like CameraPreview, Loading, and PermissionRequest to handle
 *     camera functionality, loading states, and permission handling respectively.
 */
@Suppress("DEPRECATION")
class QrEntryFragment: Fragment(){

    // binding
    private var _binding: FragmentQrBinding? = null
    private val binding get() = _binding!!
    // object
    private lateinit var scannerView: DecoratedBarcodeView
    private lateinit var permission: PermissionRequest
    private lateinit var beepManager: BeepManager
    private lateinit var qrCodeCallback: QrCodeCallback
    private lateinit var loading: Loading
    ///
    private var qrCodeResult: String? = null
    private var studentId: Long? = null
    private var isFlashlightOn = false
    private var isClassicMode: Boolean = true
    private var isScanning = false


    // View Model
    private val viewModel: QrFragmentViewModel by activityViewModels()
    private val connectivityViewModel: ConnectivityViewModel by activityViewModels()
    private val settingViewModel by activityViewModels<SettingViewModel>()

    private val cameraPermissionLauncher = this@QrEntryFragment.cameraPermissionLauncher(
        onGranted = {
            startScanner()
        },
        onDenied = {
            showSnackbar("Camera permission is required to scan QR codes!")
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = Loading(requireActivity())
        // check if dark mode or not
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES


        // init cameraAction object
        val format = listOf(BarcodeFormat.QR_CODE)
        scannerView = binding.barcodeScannerView
        scannerView.barcodeView.decoderFactory = DefaultDecoderFactory(format)
        //scannerView.statusView.visibility = View.GONE
        scannerView.setStatusText("")
        scannerView.visibility = View.INVISIBLE

        beepManager = BeepManager(requireActivity())
        qrCodeCallback = QrCodeCallback(
            beepManager,
            onQrCodeScanned = { qr ->handleQrCodeScanned(qr, isDarkMode) },
            onScanError = { e -> handleQrCodeError(e) },
            onErrorFormat = {
                pauseScanner(isDarkMode)
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )


        settingViewModel.viewMode.observe(viewLifecycleOwner) { viewMode ->
            isClassicMode = when(viewMode){
                CLASSIC -> true
                MODERN -> false
                else -> true
            }
            if (_binding != null){
                setUpBackground(isDarkMode, isClassicMode)
            }
        }

        //// Observe View model
        connectivityViewModel.networkStatus.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected){
                observerViewModel()
            }
            else{
                showSnackbar("No Internet Connection")
            }
        }

        viewAction(isDarkMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loading.stopLoading()

        // stop scanner
        if (::scannerView.isInitialized)
            scannerView.pause()

        _binding = null
    }
    //// End override method

    //////// Method
    private fun viewAction(isDarkMode: Boolean){
        permission = PermissionRequest(this)

        // PreviewView and TextView click event
        val commonClickListener = View.OnClickListener {
            binding.tvClickScan.visibility = View.GONE
            if (permission.hasCameraPermission())
                startScanner()
            else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        if (isScanning)
            binding.barcodeScannerView.setOnClickListener(commonClickListener)
        else
            binding.barcodeScannerView.setOnClickListener {
                pauseScanner(isDarkMode)
                binding.tvClickScan.visibility = View.VISIBLE
            }
        binding.tvClickScan.setOnClickListener(commonClickListener)

        binding.flashlight.apply{
            isEnabled = false
            setOnClickListener {
                isFlashlightOn = !isFlashlightOn
                toggleFlashlightIcon(isFlashlightOn, isDarkMode)
                toggleFlashlight(isFlashlightOn)
            }
        }

        /// Button cancel
        binding.btCancel.setOnClickListener {
            onButtonClearClick()
        }
        // ====== Disable edit text
        binding.edtId.isEnabled = false
        binding.edtStuName.isEnabled = false
        binding.edtMajor.isEnabled = false
        //////////////////////////////////// Button Ok
        binding.btOk.setOnClickListener {
            onButtonOkClick()
        }

        /// Refresh layout
        binding.swipeRefresh.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel
                resetLayout(isDarkMode)
                pauseScanner(isDarkMode)
            }, 1000)

        }

    }


    // set up background
    private fun setUpBackground(isDarkMode: Boolean, isClassicMode: Boolean){
        val lightColor = resources.getColor(R.color.light_color)
        val nightColor = resources.getColor(R.color.night_color)
        val transparentColor = resources.getColor(android.R.color.transparent)
        val lightButton = R.drawable.light_button_bg
        val nightButton = R.drawable.night_button_bg
        val translucentColor = if (isDarkMode)
            ContextCompat.getColor(requireContext(), R.color.translucent_black_20)
        else
            ContextCompat.getColor(requireContext(), R.color.translucent_white_20)

        val innerCardTranslucentColor = if (!isDarkMode) {
            ContextCompat.getColor(requireContext(), R.color.translucent_white_35)
        } else ContextCompat.getColor(requireContext(), R.color.translucent_black_35)

        when(isClassicMode){
            true -> {
                binding.cardSection.setCardBackgroundColor(if (isDarkMode) nightColor else lightColor)
                binding.cardCameraFrame.setCardBackgroundColor(if (isDarkMode) nightColor else lightColor)
                binding.cardStudentSection.setCardBackgroundColor(if (isDarkMode) nightColor else lightColor)

                binding.cardEntry.setCardBackgroundColor(if (isDarkMode) nightColor else lightColor)
                binding.cardExit.setCardBackgroundColor(if (isDarkMode) nightColor else lightColor)
                binding.cardTotal.setCardBackgroundColor(if (isDarkMode) nightColor else lightColor)

                binding.btOk.setBackgroundResource(if (isDarkMode) nightButton else lightButton)
                binding.btCancel.setBackgroundResource(if (isDarkMode) nightButton else lightButton)
            }
            false -> {
                // card section
                binding.cardSection.apply {
                    setCardBackgroundColor(transparentColor)
                }
                binding.blurCardSection.applyBlur(activity = requireActivity(), translucentColor = translucentColor)
                binding.blurEntryCard.applyBlur(requireActivity(), 50f, innerCardTranslucentColor)
                binding.blurExitedCard.applyBlur(requireActivity(), 50f, innerCardTranslucentColor)
                binding.blurTotalCard.applyBlur(requireActivity(), 50f, innerCardTranslucentColor)

                // camera preview view section
                binding.cardCameraFrame.apply {
                    setCardBackgroundColor(transparentColor)
                }
                binding.blurViewScanSection.applyBlur(activity = requireActivity(), translucentColor = translucentColor)

                // student detail section
                binding.cardStudentSection.apply {
                    setCardBackgroundColor(transparentColor)
                }
                binding.blurViewStudentDetail.applyBlur(activity = requireActivity(), translucentColor = translucentColor)
            }
        }
    }

    /// Observe View model
    @SuppressLint("SetTextI18n")
    private fun observerViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.onEach { state ->
                    when(state){
                        is QrFragmentState.Idle -> {}
                        is QrFragmentState.Loading -> loading.startLoading()
                        is QrFragmentState.StudentLoaded -> {
                            loading.stopLoading()
                            binding.edtId.setText(studentId.toString())
                            binding.edtStuName.setText(state.student.studentName)
                            binding.edtMajor.setText(state.student.majorName)

                            binding.edtId.isEnabled = false
                            binding.edtStuName.isEnabled = false
                            binding.edtMajor.isEnabled = false
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is QrFragmentState.Error -> {
                            loading.stopLoading()
                            binding.swipeRefresh.isRefreshing = false
                            Log.d("QRFragment", "observerViewModel: ${state.message}")
                            AlertDialog.Builder(requireContext())
                                .setMessage(state.message)
                                .setCancelable(false)
                                .setPositiveButton("OK"){dialog, _ ->
                                    dialog.dismiss()
                                }
                                .setTitle("Hello")
                                .create()
                                .show()
                        }
                        is QrFragmentState.AttentionSaved -> {
                            loading.stopLoading()
                            binding.swipeRefresh.isRefreshing = false
                            showSnackbar("Attendance saved successfully!")
                            onButtonClearClick()
                        }
                        is QrFragmentState.EntryState -> state.entry?.let {
                            loading.stopLoading()
                            binding.tvEntry.text = "${it.cardEntry[0].dataNumber}"
                            binding.tvExit.text = "${it.cardEntry[1].dataNumber}"
                            binding.tvTotal.text = "${it.cardEntry[2].dataNumber}"
                        }

                    }
                }.launchIn(lifecycleScope)
            }
        }
    }


    private fun handleQrCodeScanned(qrCode: String, isDarkMode: Boolean) {
        qrCodeResult = qrCode
        try {
            val (_, studentID) = qrCode.split("=")
            studentId = studentID.toLong()
            Log.d("QrEntry", "QR code scanned: $studentId")

            // Pause scanner
            pauseScanner(isDarkMode)
            binding.tvClickScan.visibility = View.VISIBLE

            // Load student data
            viewModel.onEvent(QrEntryEvent.LoadStudent(studentId.toString()))
        } catch (e: Exception) {
            Log.e("QrEntry", "Error processing QR code content: ${e.message}", e)
            showSnackbar("Invalid QR code format")
            qrCodeCallback.reset()
            resumeScanner()
        }
    }

    private fun handleQrCodeError(exception: Exception) {
        showSnackbar(
            "Error scanning QR code: ${exception.message}"
        )
        qrCodeCallback.reset()
        resumeScanner()
    }


    // Scanner control methods
    private fun startScanner() {
        binding.flashlight.isEnabled = true
        scannerView.visibility = View.VISIBLE
        qrCodeCallback.reset()
        scannerView.decodeContinuous(qrCodeCallback)
        scannerView.resume()
        isScanning = true
    }

    private fun pauseScanner(isDarkMode: Boolean) {
        val flashlightOff = if (isDarkMode) R.drawable.ic_flashlight_off_white_36 else R.drawable.ic_flashlight_off_black_36
        scannerView.pause()
        scannerView.visibility = View.INVISIBLE
        binding.flashlight.isEnabled = false
        isScanning = false
        scannerView.setTorchOff()
        binding.flashlight.setImageResource(flashlightOff)
    }

    private fun resumeScanner() {
        if (::scannerView.isInitialized) {
            scannerView.visibility = View.VISIBLE
            scannerView.resume()
        }
        binding.flashlight.isEnabled = false
        isScanning = false
    }

    private fun toggleFlashlight(turnOn: Boolean) {
        if (::scannerView.isInitialized) {
            if (turnOn) {
                scannerView.setTorchOn()
            } else {
                scannerView.setTorchOff()
            }
        }
    }

    private fun toggleFlashlightIcon(isOn: Boolean, isDarkMode: Boolean) {
        val flashlightOn = if (isDarkMode) R.drawable.ic_flashlight_on_white_36 else R.drawable.ic_flashlight_on_black_36
        val flashlightOff = if (isDarkMode) R.drawable.ic_flashlight_off_white_36 else R.drawable.ic_flashlight_off_black_36
        binding.flashlight.setImageResource(
            if (isOn) flashlightOn
            else flashlightOff
        )
    }

    /// On click method
    private fun onButtonOkClick(){
        var purpose = ""
        val usePC = "Use PC"
        val reading = "Reading"
        val assignment = "Assignment"
        val other = "Other"
        if (binding.cbUsePc.isChecked)
            purpose += if (purpose.isEmpty()) usePC else ", $usePC"
        if (binding.cbReadBook.isChecked)
            purpose += if (purpose.isEmpty()) reading else ", $reading"
        if (binding.cbAssignment.isChecked)
            purpose += if (purpose.isEmpty()) assignment else ", $assignment"
        if (binding.cbOther.isChecked)
            purpose += if (purpose.isEmpty()) other else ", $other"

        viewModel.onEvent(QrEntryEvent.SaveAttention(studentId.toString(), purpose))
    }

    // button clear
    private fun onButtonClearClick(){
        binding.edtId.text.clear()
        binding.edtMajor.text.clear()
        binding.edtStuName.text.clear()
        binding.cbReadBook.isChecked = false
        binding.cbUsePc.isChecked = false
        binding.cbAssignment.isChecked = false
        binding.cbOther.isChecked = false
    }

    // Reset layout to default state
    private fun resetLayout(isDarkMode: Boolean) {
        onButtonClearClick()
        binding.tvClickScan.visibility = View.VISIBLE

        // Reset scanner
        qrCodeCallback.reset()
        qrCodeResult = null
        studentId = null

        // Reset flashlight
        if (isFlashlightOn) {
            isFlashlightOn = false
            toggleFlashlight(false)
            toggleFlashlightIcon(false, isDarkMode)
        }

        // Resume scanner
        binding.swipeRefresh.isRefreshing = false
    }

    private fun showSnackbar(message: String, drawable: Drawable? = null, color: Int? = null){
        if (drawable != null && color != null){
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setIcon(drawable, color)
                .show()
        }else{
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

}