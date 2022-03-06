package com.sandorln.champion.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivitySplashBinding
import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    @Inject
    lateinit var versionManager: VersionManager

    override fun initObjectSetting() {}
    override fun initViewSetting() {}
    override fun initObserverSetting() {}

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            versionManager.initData()

            val checkVersionData = VersionManager.getVersion(this@SplashActivity).lvTotalVersion.isNotEmpty()
            if (checkVersionData) {
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashActivity, binding.imgLogo, "logo").toBundle()
                startActivity(Intent(this@SplashActivity, MainActivity::class.java), option)
            }
        }
    }
}