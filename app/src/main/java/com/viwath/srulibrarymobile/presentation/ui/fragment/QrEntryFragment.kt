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
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentQrBinding
import com.viwath.srulibrarymobile.presentation.event.QrEntryEvent
import com.viwath.srulibrarymobile.presentation.state.QrFragmentState
import com.viwath.srulibrarymobile.presentation.ui.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.QrFragmentViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.CLASSIC
import com.viwath.srulibrarymobile.presentation.viewmodel.SettingViewModel.Companion.MODERN
import com.viwath.srulibrarymobile.utils.CameraPreview
import com.viwath.srulibrarymobile.utils.applyBlur
import com.viwath.srulibrarymobile.utils.permission.PermissionLauncher.cameraPermissionLauncher
import com.viwath.srulibrarymobile.utils.permission.PermissionRequest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
    private lateinit var previewView: PreviewView
    private lateinit var cameraAction: CameraPreview
    private lateinit var permission: PermissionRequest
    private lateinit var mainActivity: MainActivity
    ///
    private var qrCodeResult: String? = null
    private var isResultHandled = false
    private var studentId: Long? = null
    private var isFlashlightOn = false
    private var isChecked = false
    private var isClassicMode: Boolean = true

    // View Model
    private val viewModel: QrFragmentViewModel by activityViewModels()
    private val connectivityViewModel: ConnectivityViewModel by activityViewModels()
    private val settingViewModel by activityViewModels<SettingViewModel>()

    private val cameraPermissionLauncher = this@QrEntryFragment.cameraPermissionLauncher(
        onGranted = {
            startCameraWithHandler()
        },
        onDenied = {
            mainActivity.showSnackbar("Camera permission is required to scan QR codes!")
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrBinding.inflate(inflater, container, false)
        previewView = binding.cameraPreviewView
        // init cameraAction object
        cameraAction = CameraPreview(requireContext(), previewView, this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = (requireActivity() as MainActivity)

        // check if dark mode or not
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        viewAction()

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::cameraAction.isInitialized){
            cameraAction.stopCamera()
        }
        mainActivity.stopLoading()
    }
    //// End override method

    //////// Method
    private fun viewAction(){
        permission = PermissionRequest(this)

        // PreviewView and TextView click event
        val commonClickListener = View.OnClickListener {
            binding.tvClickScan.visibility = View.GONE
            if (permission.hasCameraPermission())
                startCameraWithHandler()
            else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.cameraPreviewView.setOnClickListener(commonClickListener)
        binding.tvClickScan.setOnClickListener(commonClickListener)
        binding.flashlight.setOnClickListener {
            isFlashlightOn = !isFlashlightOn
            cameraAction.toggleFlashlight(isFlashlightOn)
            changeButtonIcon(isFlashlightOn)
        }
        // Radio button
        val radioCheck = View.OnClickListener { v ->
            val radioButton = v as RadioButton
            isChecked = !isChecked
            radioButton.isChecked = isChecked
        }

        binding.radioUsePc.setOnClickListener(radioCheck)
        binding.radioReadBook.setOnClickListener(radioCheck)
        binding.radioAssignment.setOnClickListener(radioCheck)
        binding.radioOther.setOnClickListener(radioCheck)

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
                resetLayout()
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

        val translucentColor = if (isDarkMode) {
            ContextCompat.getColor(requireContext(), R.color.translucent_black_20)
        }
        else {
            ContextCompat.getColor(requireContext(), R.color.translucent_white_20)
        }

        val innerCardTranslucentColor = if (!isDarkMode) {
            ContextCompat.getColor(requireContext(), R.color.translucent_white_35)
        } else {
            ContextCompat.getColor(requireContext(), R.color.translucent_black_35)
        }


        when(isClassicMode){
            true -> {
                binding.cameraPreviewView.setBackgroundResource( R.drawable.frame_camera_bg_classic)
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
                binding.cameraPreviewView.setBackgroundResource( R.drawable.frame_camera_bg_modern)
                // card section
                binding.cardSection.apply {
                    setCardBackgroundColor(transparentColor)
                    if (isDarkMode)
                        strokeColor = requireContext().getColor(R.color.purple)
                }
                binding.blurCardSection.applyBlur(activity = requireActivity(), translucentColor = translucentColor)
                binding.blurEntryCard.applyBlur(requireActivity(), 50f, innerCardTranslucentColor)
                binding.blurExitedCard.applyBlur(requireActivity(), 50f, innerCardTranslucentColor)
                binding.blurTotalCard.applyBlur(requireActivity(), 50f, innerCardTranslucentColor)

                // camera preview view section

                binding.cardCameraFrame.apply {
                    setCardBackgroundColor(transparentColor)
                    if (isDarkMode)
                        strokeColor = requireContext().getColor(R.color.purple)
                }
                binding.blurViewScanSection.applyBlur(activity = requireActivity(), translucentColor = translucentColor)

                // student detail section
                binding.cardStudentSection.apply {
                    setCardBackgroundColor(transparentColor)
                    if (isDarkMode)
                        strokeColor = requireContext().getColor(R.color.purple)
                }
                binding.blurViewStudentDetail.applyBlur(activity = requireActivity(), translucentColor = translucentColor)
            }
        }
    }

    /// Observe View model
    @SuppressLint("SetTextI18n")
    private fun observerViewModel(){
        viewModel.state.onEach { state ->
            when(state){
                is QrFragmentState.Idle -> {}
                is QrFragmentState.Loading -> mainActivity.startLoading()
                is QrFragmentState.StudentLoaded -> {
                    mainActivity.stopLoading()
                    binding.edtId.setText(studentId.toString())
                    binding.edtStuName.setText(state.student.studentName)
                    binding.edtMajor.setText(state.student.majorName)

                    binding.edtId.isEnabled = false
                    binding.edtStuName.isEnabled = false
                    binding.edtMajor.isEnabled = false
                    binding.swipeRefresh.isRefreshing = false
                }
                is QrFragmentState.Error -> {
                    mainActivity.stopLoading()
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
                    mainActivity.stopLoading()
                    binding.swipeRefresh.isRefreshing = false
                    mainActivity.showSnackbar("Attendance saved successfully!")
                    onButtonClearClick()
                }
                is QrFragmentState.EntryState -> state.entry?.let {
                    mainActivity.stopLoading()
                    binding.tvEntry.text = "${it.cardEntry[0].dataNumber}"
                    binding.tvExit.text = "${it.cardEntry[1].dataNumber}"
                    binding.tvTotal.text = "${it.cardEntry[2].dataNumber}"
                }

            }
        }.launchIn(lifecycleScope)

        //// Observe View model
        connectivityViewModel.networkStatus.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected){
                observerViewModel()
            }
            else{
                mainActivity.showTopSnackbar("No Internet Connection", true)
            }
        }

        // observe setting

    }

    ////
    private fun startCameraWithHandler(){
        isResultHandled = false
        cameraAction.startCamera { result ->
            if (!isResultHandled){
                isResultHandled = true
                qrCodeResult = result
                val (_, studentID) = result.split("=")
                studentId = studentID.toLong()
                cameraAction.stopCamera()
                binding.tvClickScan.visibility = View.VISIBLE
                viewModel.onEvent(QrEntryEvent.LoadStudent(studentId.toString()))
            }
        }
    }

    private fun changeButtonIcon(bool: Boolean){
        if (bool){
            binding.flashlight.setBackgroundResource(R.drawable.ic_white_flash_on_36)
        }
        else{
            binding.flashlight.setBackgroundResource(R.drawable.ic_white_flash_off_36)
        }
    }

    /// On click method
    private fun onButtonOkClick(){
        var purpose = ""
        val usePC = "Use PC"
        val reading = "Reading"
        val assignment = "Assignment"
        val other = "Other"
        if (binding.radioUsePc.isChecked)
            purpose += if (purpose.isEmpty()) usePC else ", $usePC"
        if (binding.radioReadBook.isChecked)
            purpose += if (purpose.isEmpty()) reading else ", $reading"
        if (binding.radioAssignment.isChecked)
            purpose += if (purpose.isEmpty()) assignment else ", $assignment"
        if (binding.radioOther.isChecked)
            purpose += if (purpose.isEmpty()) other else ", $other"

        viewModel.onEvent(QrEntryEvent.SaveAttention(studentId.toString(), purpose))
    }

    // button clear
    private fun onButtonClearClick(){
        binding.edtId.text.clear()
        binding.edtMajor.text.clear()
        binding.edtStuName.text.clear()
        binding.radioReadBook.isChecked = false
        binding.radioUsePc.isChecked = false
        binding.radioAssignment.isChecked = false
        binding.radioOther.isChecked = false
    }

    // Reset layout to default state
    private fun resetLayout() {
        onButtonClearClick()
        binding.tvClickScan.visibility = View.VISIBLE
        changeButtonIcon(isFlashlightOn)
        resetPreviewView()
        binding.swipeRefresh.isRefreshing = false
    }

    // Reset PreviewView
    private fun resetPreviewView() {
        cameraAction.stopCamera()
        isResultHandled = false
        qrCodeResult = null
        studentId = null
        previewView.visibility = View.GONE
        previewView.visibility = View.VISIBLE
    }

}