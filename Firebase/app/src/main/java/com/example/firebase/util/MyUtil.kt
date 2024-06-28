package com.example.firebase.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date


fun myCheckPermission(activity: AppCompatActivity) {

    val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.all { permission -> permission.value == true }) {
                Toast.makeText(activity, "권한 승인", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "권한 거부", Toast.LENGTH_SHORT).show()
            }
        }

    if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && (
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) !== PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) !== PackageManager.PERMISSION_GRANTED
                )
    ) {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        )
    } else if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_MEDIA_IMAGES
        ) !== PackageManager.PERMISSION_GRANTED

    ) {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        )
    } else if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }


}

fun dateToString(date: Date): String {
    val format = SimpleDateFormat("yyyy-MM-dd")
    return format.format(date)
}