package com.sandorln.champion.view.activity

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivitySplashLoadingBinding
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.view.base.BaseActivity
import com.sandorln.champion.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashLoadingActivity : BaseActivity<ActivitySplashLoadingBinding>(R.layout.activity_splash_loading) {
    /* ViewModels */
    private val splashViewModel: SplashViewModel by viewModels()

    override fun initObjectSetting() {}
    override fun initViewSetting() {}
    override fun initObserverSetting() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                splashViewModel
                    .hasNewLolVersion
                    .collectLatest { result ->
                        when (result) {
                            is ResultData.Failed -> {
                                showAlertDialog(
                                    message = "버전 정보를 받아오지 못했습니다",
                                    positiveBtnName = "재시도",
                                    negativeBtnName = "종료"
                                ) { isPositiveBtn ->
                                    if (isPositiveBtn)
                                        splashViewModel.refreshHasNewLolVersion()
                                    else
                                        finish()
                                }
                            }
                            is ResultData.Success -> {
                                val hasNewLolVersion = result.data == true

                                if (hasNewLolVersion)
                                    showAlertDialog(
                                        message = "설정된 롤 버전보다 최신 롤 버전이 있습니다\n최신 롤 버전으로 변경하시겠습니까?",
                                        positiveBtnName = "네",
                                        negativeBtnName = "아니요"
                                    ) { isPositiveBtn ->
                                        if (isPositiveBtn)
                                            lifecycleScope.launchWhenResumed {
                                                splashViewModel.changeNewestVersion()
                                                startMainActivity()
                                            }
                                        else
                                            startMainActivity()
                                    }
                                else
                                    startMainActivity()
                            }
                            else -> {}
                        }
                    }
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this@SplashLoadingActivity, MainActivity::class.java))
    }
}