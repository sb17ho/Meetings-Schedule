package com.example.meetingschedule

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.meetingschedule.databinding.ActivityMainBinding
import com.example.meetingschedule.viewModel.SharedViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(this)[SharedViewModel::class.java]
    }
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
            .navController
    }
    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mainBinding.bottomNavBar.setupWithNavController(navController)

        requestPermission()
    }

    private fun requestPermission() {
        val permission: Boolean = checkReadContactsPermission()

        if (permission) {
            viewModel.setReadContactsPermission(permission)
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                viewModel.getPermissionCode()
            )
        }
    }

    private fun checkReadContactsPermission() =
        checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

    fun setActionBarTitle(title: String?) {
        supportActionBar!!.title = title
    }
}