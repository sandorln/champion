package com.sandorln.champion.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sandorln.champion.R
import com.sandorln.champion.api.data.CharacterData
import kotlinx.android.synthetic.main.item_champion_skin.view.*

class ChampSkinAdapter(var characterData: CharacterData = CharacterData()) : RecyclerView.Adapter<ChampSkinAdapter.ChampSkinViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampSkinViewHolder =
        ChampSkinViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_champion_skin, parent, false))

    override fun getItemCount(): Int = characterData.cSkins.size

    override fun onBindViewHolder(holder: ChampSkinViewHolder, position: Int) {
        with(holder.itemView) {
            Glide.with(context)
                .load("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/${characterData.cId}_${characterData.cSkins[position].skNum}.jpg")
                .thumbnail(0.1f).into(img_champion_full)

            /* 스킨 명이 default 일 시 기본 챔피언 이름을 보여줌 */
            tx_skin_name.text = if (characterData.cSkins[position].skName == "default") characterData.cName else characterData.cSkins[position].skName
        }
    }

    inner class ChampSkinViewHolder(view: View) : RecyclerView.ViewHolder(view)
}