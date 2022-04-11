package com.sandorln.champion.view.activity

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivitySplashLoadingBinding
import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashLoadingActivity : BaseActivity<ActivitySplashLoadingBinding>(R.layout.activity_splash_loading) {
    @Inject
    lateinit var versionManager: VersionManager

    override fun initObjectSetting() {}
    override fun initViewSetting() {}
    override fun initObserverSetting() {}

    override fun onResume() {
        super.onResume()
        initVersionData()
    }

    private fun initVersionData() {
        lifecycleScope.launchWhenResumed {
            try {
                versionManager.initData()

                val checkVersionData = VersionManager.getVersion(this@SplashLoadingActivity).lvTotalVersion.isNotEmpty()
                if (checkVersionData) {
                    val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashLoadingActivity, binding.imgLogo, "logo").toBundle()
                    startActivity(Intent(this@SplashLoadingActivity, MainActivity::class.java), option)
                }
            } catch (e: Exception) {
                AlertDialog
                    .Builder(this@SplashLoadingActivity)
                    .setTitle("오류")
                    .setMessage("오류가 발생하였습니다.\n다시 시도해주세요")
                    .setPositiveButton("다시 시도") { _, _ ->
                        initVersionData()
                    }
                    .setNegativeButton("취소") { _, _ -> finish() }
                    .show()
            }
        }
    }
}