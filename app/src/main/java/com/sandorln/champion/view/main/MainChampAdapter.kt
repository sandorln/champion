package com.sandorln.champion.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sandorln.champion.R
import com.sandorln.champion.api.LolDataService
import com.sandorln.champion.data.CharacterData
import kotlinx.android.synthetic.main.item_champion_icon.view.*

class MainChampAdapter : RecyclerView.Adapter<MainChampAdapter.MainChampionViewHolder>() {

    private var _selectChampion = MutableLiveData<CharacterData>().apply { value = CharacterData() }
    val selectChampion : LiveData<CharacterData> get() = _selectChampion

    var championList: List<CharacterData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainChampionViewHolder =
        MainChampionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_champion_icon, parent, false))
            .apply {
                val layoutParams = itemView.layoutParams
                layoutParams.height = (parent.width / 6.0).toInt()
                itemView.layoutParams = layoutParams
            }

    override fun getItemCount(): Int = championList.size

    override fun onBindViewHolder(holder: MainChampionViewHolder, position: Int) {
        with(holder.itemView) {
            setOnClickListener { _selectChampion.postValue(championList[position]) }

            /* 챔피언 버전에 맞게 URL 수정 후 이미지 불러오기 */
            val champVersion = LolDataService.lolVersion!!.lvCategory.cvChampion

            Glide.with(context)
                .load("http://ddragon.leagueoflegends.com/cdn/$champVersion/img/champion/${championList[position].cId}.png")
                .thumbnail(0.1f).placeholder(R.drawable.ic_launcher_foreground).into(img_champion_icon)

            tx_champion_name.text = championList[position].cName
            this.requestLayout()
        }
    }


    inner class MainChampionViewHolder(view: View) : RecyclerView.ViewHolder(view)
}