package com.dhikapro.piter.app.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionCheck {

    private val permissionCode = 123

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
    )

    fun checkPermissions(activity: AppCompatActivity): Boolean {
        val permissionList = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission)
            }
        }

        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionList.toTypedArray(), permissionCode)
            return false
        }

        return true
    }

    fun showPermissionsToRequest(activity: AppCompatActivity): String {
        val permissionNames = permissions.joinToString(", ") {
            when (it) {
                Manifest.permission.ACCESS_COARSE_LOCATION -> "Location (Coarse)"
                Manifest.permission.ACCESS_FINE_LOCATION -> "Location (Fine)"
                Manifest.permission.CAMERA -> "Camera"
                else -> ""
            }
        }

        return "Berikan Izin Aplikasi Terhadap $permissionNames"
    }
}