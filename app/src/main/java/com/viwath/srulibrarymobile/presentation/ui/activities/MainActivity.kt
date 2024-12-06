package com.viwath.srulibrarymobile.presentation.ui.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
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

    private var activeFragmentTag: String? = null

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

        if (savedInstanceState == null){
            loadFragment(dashboardFragment, DashboardFragment::class.java.simpleName)
            Log.d("SavedInstanceState", "onCreate: $activeFragmentTag")
        }else{
            activeFragmentTag = savedInstanceState.getString(ACTIVE_FRAGMENT_TAG)
            activeFragmentTag?.let { restoreFragment(it) }
        }

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.btDashboard -> {
                    loadFragment(dashboardFragment, DashboardFragment::class.java.simpleName)
                    true
                }
                R.id.btQrEntry -> {
                    loadFragment(qrEntryFragment, QrEntryFragment::class.java.simpleName)
                    true
                }
                R.id.btSetting -> {
                    // Load setting fragment
                    loadFragment(settingFragment, SettingFragment::class.java.simpleName)
                    true
                }
                R.id.btStudent -> {
                    // Load student fragment
                    true
                }
                R.id.btBook -> {
                    // Load book fragment
                    loadFragment(bookFragment, BookFragment::class.java.simpleName)
                    true
                }
                else -> false
            }
        }

    }

    // save fragment
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ACTIVE_FRAGMENT_TAG, activeFragmentTag)
    }

    // load fragment
    private fun loadFragment(fragment: Fragment, tag: String){
        if (activeFragmentTag != tag) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentFrame, fragment, tag)
                addToBackStack(null)
                commit()
            }
            activeFragmentTag = tag
        }
    }

    // restore fragment
    private fun restoreFragment(tag: String){
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null){
            loadFragment(fragment, tag)
        }else {
            // Create a new fragment instance if it's missing
            val newFragment = when (tag) {
                DashboardFragment::class.java.simpleName -> dashboardFragment
                QrEntryFragment::class.java.simpleName -> qrEntryFragment
                BookFragment::class.java.simpleName -> bookFragment
                SettingFragment::class.java.simpleName -> settingFragment
                else -> dashboardFragment // Fallback to dashboard
            }
            loadFragment(newFragment, tag)
        }
    }

    companion object{
        private const val ACTIVE_FRAGMENT_TAG = "active_fragment"
    }

}
