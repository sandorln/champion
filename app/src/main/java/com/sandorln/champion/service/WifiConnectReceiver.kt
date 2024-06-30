package com.sandorln.champion.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class WifiConnectReceiver(val onWifiConnectListener: (isWifiConnect: Boolean) -> Unit) : BroadcastReceiver() {
    /**
     * 무료 네트워크 상태인지 확인
     * isActiveNetworkMetered = true(유료) false(무료)
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        val isWifiConnect = !((context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.isActiveNetworkMetered ?: true)
        onWifiConnectListener(isWifiConnect)
    }
}