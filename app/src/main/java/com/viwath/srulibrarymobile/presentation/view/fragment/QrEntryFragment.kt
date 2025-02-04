/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.view.fragment

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragmentQrBinding
import com.viwath.srulibrarymobile.presentation.event.QrEntryEvent
import com.viwath.srulibrarymobile.presentation.state.QrFragmentState
import com.viwath.srulibrarymobile.presentation.view.activities.MainActivity
import com.viwath.srulibrarymobile.presentation.viewmodel.ConnectivityViewModel
import com.viwath.srulibrarymobile.presentation.viewmodel.QrFragmentViewModel
import com.viwath.srulibrarymobile.utils.CameraPreview
import com.viwath.srulibrarymobile.utils.PermissionRequest
import com.viwath.srulibrarymobile.utils.connectivity.Status
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
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCameraWithHandler()
        } else {
            Snackbar.make(binding.root, "Camera permission is required to scan QR codes!", Snackbar.LENGTH_LONG).show()
        }
    }
    ///
    private var qrCodeResult: String? = null
    private var isResultHandled = false
    private var studentId: Long? = null
    private var isFlashlightOn = false
    private var isChecked = false

    // View Model
    private val viewModel: QrFragmentViewModel by activityViewModels()
    private val connectivityViewModel: ConnectivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrBinding.inflate(inflater, container, false)
        previewView = binding.cameraPreview
        // init cameraAction object
        cameraAction = CameraPreview(requireContext(), previewView, this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = (requireActivity() as MainActivity)

        // check if dark mode or not
        val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        setUpBackground(isDarkTheme)

        permission = PermissionRequest(this)

        // PreviewView and TextView click event
        val commonClickListener = View.OnClickListener {
            binding.tvClickScan.visibility = View.GONE
            if (permission.hasCameraPermission())
                startCameraWithHandler()
            else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.cameraPreview.setOnClickListener(commonClickListener)
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
        //// Observe View model
        connectivityViewModel.networkStatus.observe(viewLifecycleOwner) { status ->
            when(status){
                Status.DISCONNECTED -> mainActivity.showTopSnackbar("No Internet Connection", true)
                else -> observerViewModel()
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

    // set up background
    private fun setUpBackground(isDarkTheme: Boolean){
        val lightColor = resources.getColor(R.color.light_color)
        val nightColor = resources.getColor(R.color.night_color)
        val lightButton = R.drawable.light_button_bg
        val nightButton = R.drawable.night_button_bg
        val nightBoxBackground = R.drawable.night_box_bg
        val lightBoxBackground = R.drawable.light_box_bg

        binding.relativeCamera.setBackgroundResource(if (isDarkTheme) nightBoxBackground else lightBoxBackground)
        binding.linearStudentDetail.setBackgroundResource(if (isDarkTheme) nightBoxBackground else lightBoxBackground)

        binding.cardEntry.setCardBackgroundColor(if (isDarkTheme) nightColor else lightColor)
        binding.cardExit.setCardBackgroundColor(if (isDarkTheme) nightColor else lightColor)
        binding.cardTotal.setCardBackgroundColor(if (isDarkTheme) nightColor else lightColor)

        binding.btOk.setBackgroundResource(if (isDarkTheme) nightButton else lightButton)
        binding.btCancel.setBackgroundResource(if (isDarkTheme) nightButton else lightButton)
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
                    showSuccessMessage()
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

    // Snack bar
    private fun showSuccessMessage() {
        Snackbar.make(binding.root, "Attendance saved successfully!", Snackbar.LENGTH_LONG).show()
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