/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.presentation.ui.activities

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePinActivity : AppCompatActivity() {
    /*private lateinit var binding: ActivityPinCreationBinding

    private val viewModel: PinViewModel by viewModels()

    private lateinit var pinBoxes: List<TextView>
    private val pinInput: StringBuilder = StringBuilder()

    private var enteredPin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pinBoxes = listOf(
            binding.pin1,
            binding.pin2,
            binding.pin3,
            binding.pin4,
            binding.pin5,
            binding.pin6
        )

        val buttons = listOf(
            binding.btn0,
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5,
            binding.btn6,
            binding.btn7,
            binding.btn8,
            binding.btn9
        )

        for (bt in buttons){
            bt.setOnClickListener {
                onNumberClick(bt.text.toString())
            }
        }

        binding.btnDel.setOnClickListener {
            onDeleteClick()
        }
    }

    private fun onNumberClick(number: String) {
        if (pinInput.length < 6){
            pinInput.append(number)
            updatePinBoxes()
        }

        if (pinInput.length == 6){
            lifecycleScope.launch {
                delay(1000)
                if (viewModel.isPinSet()){
                    validatePin(pinInput.toString())
                }else{
                    if (enteredPin == null ){
                        enteredPin = pinInput.toString()
                        pinInput.clear()
                        updatePinBoxes()
                        binding.tvPin.text = getString(R.string.confirm_pin)
                    }
                    else{
                        if (enteredPin == pinInput.toString()){
                            setPin(enteredPin!!)
                        }else{
                            showSnackBar("PIN does not match, try again")
                            enteredPin = null
                        }
                        pinInput.clear()
                        updatePinBoxes()
                    }
                }
            }
        }
    }

    private fun onDeleteClick(){
        if (pinInput.isNotEmpty()){
            pinInput.deleteCharAt(pinInput.length - 1)
            updatePinBoxes()
        }
    }

    private fun updatePinBoxes() {
        for (i in pinBoxes.indices){
            pinBoxes[i].background = if (i < pinInput.length)
                ContextCompat.getDrawable(this, R.drawable.pin_box_filled_bg)
                else ContextCompat.getDrawable(this, R.drawable.pin_box_bg)
        }
    }

    private fun validatePin(enteredPin: String){
        showSnackBar("Pin: $enteredPin")

        // do validate here
    }

    private fun setPin(enteredPin: String){
        viewModel.savePin(enteredPin)
        Toast.makeText(this, "PIN set successfully", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showSnackBar(str: String){
        Snackbar.make(binding.root, str, Snackbar.LENGTH_SHORT).show()
    }*/
}