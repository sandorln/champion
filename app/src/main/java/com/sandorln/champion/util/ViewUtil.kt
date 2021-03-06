package com.sandorln.champion.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sandorln.champion.di.GlideApp

fun ImageView.setChampionSplash(championId: String, skinNum: String) {
    GlideApp.with(context)
        .load("http://ddragon.leagueoflegends.com/cdn/img/champion/splash/${championId}_${skinNum}.jpg")
        .thumbnail(0.5f)
        .fitCenter()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

@BindingAdapter(value = ["championSkinDrawable", "championId"], requireAll = false)
fun ImageView.setChampionSplash(championSkinDrawable: Drawable?, championId: String?) {
    if (championSkinDrawable == null) {
        GlideApp.with(context)
            .load("http://ddragon.leagueoflegends.com/cdn/img/champion/splash/${championId}_0.jpg")
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
    } else {
        setImageDrawable(championSkinDrawable)
    }
}

fun PopupWindow.showStringListPopup(popupTargetView: View, versionList: List<String>, onClickVersionListener: (version: String) -> Unit) {
    val context = popupTargetView.context
    val adapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, versionList)
    val listViewSort = ListView(context)
    listViewSort.adapter = adapter

    // set on item selected
    listViewSort.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
        val selectVersion = adapterView.adapter.getItem(i).toString()
        onClickVersionListener(selectVersion)
        dismiss()
    }

    isFocusable = true
    width = 800
    height = 1300

    contentView = listViewSort
    showAsDropDown(popupTargetView)
}