package com.viwath.srulibrarymobile.presentation.ui.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.viwath.srulibrarymobile.R
import com.viwath.srulibrarymobile.databinding.ActivityMainBinding
import com.viwath.srulibrarymobile.presentation.ui.fragment.BookFragment
import com.viwath.srulibrarymobile.presentation.ui.fragment.DashboardFragment
import com.viwath.srulibrarymobile.presentation.ui.fragment.QrEntryFragment
import com.viwath.srulibrarymobile.presentation.ui.fragment.SettingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dashboardFragment: DashboardFragment
    private lateinit var qrEntryFragment: QrEntryFragment
    private lateinit var bookFragment: BookFragment
    private lateinit var settingFragment: SettingFragment

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init fragment
        dashboardFragment = DashboardFragment()
        qrEntryFragment = QrEntryFragment()
        bookFragment = BookFragment()
        settingFragment = SettingFragment()

        binding.bottomNavView.background = null

        loadFragment(dashboardFragment)

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.btDashboard -> {
                    loadFragment(dashboardFragment)
                    true
                }
                R.id.btQrEntry -> {
                    loadFragment(qrEntryFragment)
                    true
                }
                R.id.btSetting -> {
                    // Load setting fragment
                    loadFragment(settingFragment)
                    true
                }
                R.id.btStudent -> {
                    // Load student fragment
                    true
                }
                R.id.btBook -> {
                    // Load book fragment
                    loadFragment(bookFragment)
                    true
                }
                else -> false
            }
        }

    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentFrame, fragment)
            commit()
        }
    }

}
