package com.sandorln.champion.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.sandorln.champion.application.ChampionApplication

abstract class BaseFragment<T : ViewDataBinding>(@LayoutRes private val layoutId: Int) : Fragment() {
    lateinit var binding: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObjectSetting()
        initViewSetting()
        initObserverSetting()
    }

    abstract fun initObjectSetting()
    abstract fun initViewSetting()
    abstract fun initObserverSetting()

    private fun getChampionApplication(): ChampionApplication? = requireContext().applicationContext as? ChampionApplication

    fun showAlertDialog(
        message: String,
        title: String = "",
        positiveBtnName: String = "확인",
        negativeBtnName: String = "취소",
        onClickListener: (isPositiveBtn: Boolean) -> Unit = {}
    ) = getChampionApplication()?.showAlertDialog(requireContext(), message, title, positiveBtnName, negativeBtnName, onClickListener)

    fun showToast(message: String) = getChampionApplication()?.showToast(message)
}