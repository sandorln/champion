package com.sandorln.champion.application

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.sandorln.champion.service.WifiConnectReceiver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

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

    val isWifiConnectFlow = callbackFlow {
        val wifiConnectReceiver = WifiConnectReceiver { isWifiConnect -> trySend(isWifiConnect) }
        val intentFilter = IntentFilter().apply {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
        }
        registerReceiver(wifiConnectReceiver, intentFilter)

        awaitClose { unregisterReceiver(wifiConnectReceiver) }
    }
}