package com.viwath.srulibrarymobile.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.viwath.srulibrarymobile.databinding.ActivityChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity(){

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvGoLogin.setOnClickListener{
            startActivity(Intent(this@ChangePasswordActivity, LoginActivity::class.java))
            this.finish()
        }
        binding.btConfirm.setOnClickListener{

        }

    }

}