package com.sandorln.champion.view.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.sandorln.champion.application.ChampionApplication
import kotlinx.coroutines.flow.Flow

abstract class BaseActivity<T : ViewDataBinding>(@LayoutRes private val layoutId: Int) : AppCompatActivity() {
    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this

        initObjectSetting()
        initViewSetting()
        initObserverSetting()
    }

    abstract fun initObjectSetting()
    abstract fun initViewSetting()
    abstract fun initObserverSetting()

    private fun getChampionApplication(): ChampionApplication? = applicationContext as? ChampionApplication

    fun showAlertDialog(
        message: String,
        title: String = "",
        positiveBtnName: String = "확인",
        negativeBtnName: String = "취소",
        onClickListener: (isPositiveBtn: Boolean) -> Unit = {}
    ) = getChampionApplication()?.showAlertDialog(this, message, title, positiveBtnName, negativeBtnName, onClickListener)

    fun showToast(message: String) = getChampionApplication()?.showToast(message)
}