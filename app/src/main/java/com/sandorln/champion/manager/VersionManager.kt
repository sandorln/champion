package com.sandorln.champion.manager

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.sandorln.champion.model.VersionLol
import com.sandorln.champion.network.VersionService
import com.sandorln.champion.view.activity.SplashActivity

class VersionManager(private val versionService: VersionService) {
    companion object {
        private var versionLol: VersionLol? = null
        fun getVersion(context: Context): VersionLol {
            return versionLol ?: let {
                AlertDialog
                    .Builder(context)
                    .setTitle("알림")
                    .setMessage("롤 버전을 가져올 수 없습니다")
                    .setPositiveButton("다시 접속") { _, _ ->
                        val intent = Intent(context, SplashActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(intent)
                        VersionLol()
                    }
                    .setNegativeButton("종료") { _, _ ->
                        (context as? Activity)?.apply {
                            moveTaskToBack(true)
                            finishAndRemoveTask()
                            android.os.Process.killProcess(android.os.Process.myPid())
                        }
                    }
                    .show()

                VersionLol()
            }
        }

        fun getVersion(): VersionLol = versionLol ?: throw Exception()
    }

    suspend fun initData() {
        try {
            if (versionLol == null)
                versionLol = versionService.getVersion()
        } catch (e: Exception) {
            throw  e
        }
    }
}