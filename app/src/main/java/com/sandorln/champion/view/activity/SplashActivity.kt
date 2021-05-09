package com.sandorln.champion.view.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityOptionsCompat
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivitySplashBinding
import com.sandorln.champion.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    override suspend fun initObjectSetting() {}
    override suspend fun initViewSetting() {}
    override suspend fun initObserverSetting() {}

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.imgLogo, "logo").toBundle()
            startActivity(Intent(this, MainActivity::class.java), option)
        }, 750)
    }
}