package uz.ilmiddin1701.asalariapp.utils

import android.os.Build

inline fun <T> sdk26AdnUp(onSdk26: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk26()
    } else null
}