package com.sandorln.champion.view.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseActivity<T : ViewDataBinding>(@LayoutRes private val layoutId: Int) : AppCompatActivity() {
    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this


        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.Default) {
                initObjectSetting()
            }

            withContext(Dispatchers.Main) {
                initViewSetting()
                initObserverSetting()
            }
        }
    }

    abstract suspend fun initObjectSetting()
    abstract suspend fun initViewSetting()
    abstract suspend fun initObserverSetting()
}