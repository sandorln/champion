package com.sandorln.champion.view.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ItemChampionStatusBinding

class ChampionStatusAdapter(
    var status: JsonArray = JsonArray(),
    var otherStatus: JsonArray = JsonArray()
) : RecyclerView.Adapter<ChampionStatusAdapter.ChampionStatusViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionStatusViewHolder =
        ChampionStatusViewHolder(ItemChampionStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionStatusViewHolder, position: Int) {
        val context = holder.itemView.context
        with(holder.binding) {
            try {
                val originStatusJson = status.get(position).asJsonObject
                val originStatusKey = originStatusJson.keySet().first()
                val originStatusValue = originStatusJson[originStatusKey].asDouble

                val otherStatusJson = otherStatus.get(position).asJsonObject
                val otherStatusKey = otherStatusJson.keySet().first()
                val otherStatusValue = otherStatusJson[otherStatusKey].asDouble

                val stringId = context.resources.getIdentifier(originStatusKey, "string", context.packageName)
                tvStatusKey.text = context.getString(stringId)
                tvStatusValue.text = originStatusValue.toString()

                val drawable = when {
                    originStatusValue > otherStatusValue -> ContextCompat.getDrawable(context, R.drawable.ic_up_arrow)
                    originStatusValue < otherStatusValue -> ContextCompat.getDrawable(context, R.drawable.ic_down_arrow)
                    else -> null
                }
                val tint = when {
                    originStatusValue > otherStatusValue -> ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                    originStatusValue < otherStatusValue -> ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                    else -> null
                }
                imgStatusIcon.setImageDrawable(drawable)
                imgStatusIcon.imageTintList = tint

            } catch (e: Exception) {
                tvStatusKey.text = context.getString(R.string.error)
                tvStatusValue.text = context.getString(R.string.error)
                imgStatusIcon.setImageDrawable(null)
            }
        }
    }

    override fun getItemCount(): Int = status.size()
    class ChampionStatusViewHolder(val binding: ItemChampionStatusBinding) : RecyclerView.ViewHolder(binding.root)
}