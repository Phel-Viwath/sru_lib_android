package com.viwath.srulibrarymobile.presentation.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.FragementQrBinding
import com.viwath.srulibrarymobile.presentation.event.QrEntryEvent
import com.viwath.srulibrarymobile.presentation.state.QrFragmentState
import com.viwath.srulibrarymobile.presentation.viewmodel.QrFragmentViewModel
import com.viwath.srulibrarymobile.utils.CameraPreview
import com.viwath.srulibrarymobile.utils.PermissionRequest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class QrEntryFragment: Fragment(){

    // binding
    private var _binding: FragementQrBinding? = null
    private val binding get() = _binding!!
    // object
    private lateinit var previewView: PreviewView
    private lateinit var cameraAction: CameraPreview
    private lateinit var permission: PermissionRequest
    ///
    private var qrCodeResult: String? = null
    private var isResultHandled = false
    private var studentId: Long? = null
    private var isFlashlightOn = false
    private var isChecked = false

    // View Model
    private val viewModel: QrFragmentViewModel by lazy {
        ViewModelProvider(requireActivity())[QrFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragementQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // check if dark mode or not
        val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        setUpBackground(isDarkTheme)

        permission = PermissionRequest(this)
        previewView = binding.cameraPreview
        // init cameraAction object
        cameraAction = CameraPreview(requireContext(), previewView, this)
        // PreviewView and TextView click event
        val commonClickListener = View.OnClickListener {
            binding.tvClickScan.visibility = View.GONE
            if (permission.hasCameraPermission())
                startCameraWithHandler()
            else permission.requestPermission(0)
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
            resetLayout()
        }
        //// Observe View model
        observerViewModel()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permission.handlePermissionResult(requestCode, grantResults){
            cameraAction.startCamera { result ->
                qrCodeResult = result
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAction.stopCamera()
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
                is QrFragmentState.Empty -> {
                    //binding.swipeRefresh.isRefreshing = true
                }
                is QrFragmentState.Loading -> {
                    //binding.swipeRefresh.isRefreshing = true
                }
                is QrFragmentState.StudentLoaded -> {
                    binding.edtId.setText(studentId.toString())
                    binding.edtStuName.setText(state.student.studentName)
                    binding.edtMajor.setText(state.student.majorName)

                    binding.edtId.isEnabled = false
                    binding.edtStuName.isEnabled = false
                    binding.edtMajor.isEnabled = false

                    binding.swipeRefresh.isRefreshing = false
                }
                is QrFragmentState.Error -> {
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
                    binding.swipeRefresh.isRefreshing = false
                    showSuccessMessage()
                    onButtonClearClick()
                }
                is QrFragmentState.EntryState -> state.entry?.let {
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
        if (permission.hasCameraPermission()) {
            startCameraWithHandler()
        }
    }

}