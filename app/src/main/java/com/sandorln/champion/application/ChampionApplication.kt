package com.sandorln.champion.application

import android.app.Application
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChampionApplication : Application() {
    lateinit var toast: Toast
    fun showToast(message: String) {
        if (::toast.isInitialized)
            toast.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()
    }
}