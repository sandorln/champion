package com.sandorln.champion.view.splash

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ASplashBinding
import com.sandorln.champion.view.BaseActivity
import com.sandorln.champion.view.main.MainActivity
import com.sandorln.champion.viewmodel.VersionViewModel
import com.sandorln.champion.viewmodel.factory.ViewModelFactory

class SplashActivity : BaseActivity<ASplashBinding>() {
    override fun getLayout(): Int = R.layout.a_splash

    private val versionViewModel: VersionViewModel by lazy { ViewModelProvider(this, ViewModelFactory(application))[VersionViewModel::class.java] }

    override fun initBindingSetting() {}
    override fun initObjectSetting() {}
    override fun initViewSetting() {}

    override fun initObserverSetting() {
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

        versionViewModel.getVersion {
            val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.imgLogo, "logo").toBundle()
            startActivity(Intent(this, MainActivity::class.java), option)
            finish()
        }
    }
}