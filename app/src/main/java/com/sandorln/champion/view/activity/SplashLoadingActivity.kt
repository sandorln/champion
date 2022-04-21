package com.sandorln.champion.view.activity

import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivitySplashLoadingBinding
import com.sandorln.champion.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashLoadingActivity : BaseActivity<ActivitySplashLoadingBinding>(R.layout.activity_splash_loading) {
    override fun initObjectSetting() {}
    override fun initViewSetting() {}
    override fun initObserverSetting() {
        lifecycleScope.launchWhenResumed {
            delay(750)
            val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashLoadingActivity, binding.imgLogo, "logo").toBundle()
            startActivity(Intent(this@SplashLoadingActivity, MainActivity::class.java))
        }
    }
}