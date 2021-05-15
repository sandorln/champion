package com.sandorln.champion.view.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivitySplashBinding
import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    @Inject
    lateinit var versionManager: VersionManager

    override suspend fun initObjectSetting() {}
    override suspend fun initViewSetting() {}
    override suspend fun initObserverSetting() {}

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            try {
                val version = withContext(Dispatchers.IO) {
                    versionManager.getVersion()
                }

                if (version.lvTotalVersion.isNotEmpty()) {
                    val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashActivity, binding.imgLogo, "logo").toBundle()
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java), option)
                } else
                    throw Exception()

            } catch (e: Exception) {
                AlertDialog
                    .Builder(this@SplashActivity)
                    .setTitle("알림")
                    .setMessage("롤 버전을 가져올 수 없습니다")
                    .setPositiveButton("확인") { _, _ ->
                        finish()
                    }
            }
        }
    }
}