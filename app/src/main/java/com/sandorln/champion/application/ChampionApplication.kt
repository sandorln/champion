package com.sandorln.champion.application

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    fun showAlertDialog(
        context: Context,
        message: String,
        title: String = "",
        positiveBtnName: String = "확인",
        negativeBtnName: String = "취소",
        onClickListener: (isPositiveBtn: Boolean) -> Unit = {}
    ) {
        AlertDialog
            .Builder(context).apply {
                if (title.isNotEmpty()) setTitle(title)
                if (message.isNotEmpty()) setMessage(message)

                setPositiveButton(positiveBtnName) { dialog, _ ->
                    onClickListener(true)
                    dialog.dismiss()
                }

                setNegativeButton(negativeBtnName) { dialog, _ ->
                    onClickListener(false)
                    dialog.dismiss()
                }

                setCancelable(false)
            }
            .create()
            .show()
    }
}