package com.sandorln.champion.view.activity

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivitySplashBinding
import com.sandorln.champion.view.base.BaseActivity
import com.sandorln.champion.viewmodel.VersionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    private val versionViewModel: VersionViewModel by viewModels()

    override suspend fun initViewModelSetting() {}
    override suspend fun initObjectSetting() {}
    override suspend fun initViewSetting() {}
    override suspend fun initObserverSetting() {
        versionViewModel.errorMsg.observe(this, Observer { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                AlertDialog
                    .Builder(this)
                    .setTitle("Error")
                    .setMessage(errorMsg)
                    .setPositiveButton("Okay") { _, _ -> finish() }
                    .create()
                    .show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        versionViewModel.getVersion {
            val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.imgLogo, "logo").toBundle()
            startActivity(Intent(this, MainActivity::class.java), option)
            finish()
        }
    }
}