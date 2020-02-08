package com.sandorln.champion.view

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = Color.BLACK

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayout())
        binding.lifecycleOwner = this

        initBindingSetting()
        initObjectSetting()
        initViewSetting()
        initObserverSetting()
    }

    abstract fun getLayout(): Int

    abstract fun initBindingSetting()
    abstract fun initObjectSetting()
    abstract fun initViewSetting()
    abstract fun initObserverSetting()
}