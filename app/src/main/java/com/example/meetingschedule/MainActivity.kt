package com.example.meetingschedule

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.meetingschedule.viewModel.SharedViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(this)[SharedViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestPermission()
    }

    fun requestPermission() {
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