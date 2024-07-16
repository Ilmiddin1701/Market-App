package uz.ilmiddin1701.asalariapp.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData

object MyData {
    var writePermissionGranted = false
    var umumiyNarx = MutableLiveData<Long>()
    var isScanner = MutableLiveData<Boolean>()
    lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
}