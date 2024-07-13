package uz.ilmiddin1701.asalariapp

import androidx.activity.result.ActivityResultLauncher

object MyData {
    var writePermissionGranted = false
    lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
}