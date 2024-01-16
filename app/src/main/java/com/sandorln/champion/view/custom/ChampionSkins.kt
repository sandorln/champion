package com.sandorln.champion.view.custom

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.get
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.sandorln.champion.databinding.CustomChampionSkinsBinding
import com.sandorln.champion.view.adapter.ChampionFullSkinAdapter
import com.sandorln.model.data.champion.ChampionSkin
import kotlin.math.abs

class ChampionSkins : FrameLayout {
    val binding = CustomChampionSkinsBinding.inflate(LayoutInflater.from(context), this, true)
    val skinAdapter = ChampionFullSkinAdapter()
    var championId: String = "0"
        set(value) {
            skinAdapter.championId = value
            skinAdapter.notifyItemRangeChanged(0, skinAdapter.itemCount)
            field = value
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        with(binding.vpFullSkin) {
            adapter = skinAdapter

            /* 양옆 미리보기 처리 */
            val pageMarginPx = resources.getDimensionPixelOffset(com.sandorln.champion.R.dimen.padding_xlarge)
            val pagerWidth = resources.getDimensionPixelOffset(com.sandorln.champion.R.dimen.padding_middle)

            setPageTransformer { page, position ->
                page.translationX = -(pageMarginPx + pagerWidth) * position
                page.scaleY = 1 - (0.10f * abs(position))
            }

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.right = pageMarginPx
                    outRect.left = pageMarginPx
                }
            })
        }

        binding.vPre.setOnClickListener {
            try {
                val index = binding.vpFullSkin.currentItem
                if (index > 0)
                    binding.vpFullSkin.setCurrentItem(index - 1, true)
            } catch (e: Exception) {

            }
        }

        binding.vNext.setOnClickListener {
            try {
                val index = binding.vpFullSkin.currentItem
                if (index < skinAdapter.currentList.size - 1)
                    binding.vpFullSkin.setCurrentItem(index + 1, true)
            } catch (e: Exception) {

            }
        }
    }
}

@BindingAdapter("championId")
fun ChampionSkins.setChampionId(championId: String) {
    this.championId = championId
}

@BindingAdapter(value = ["skins", "changeSelectSkin"], requireAll = false)
fun ChampionSkins.setSkins(skins: List<ChampionSkin>?, changeSelectSkin: (drawable: Drawable?, skinName: String?) -> Unit?) {
    skinAdapter.submitList(skins)
    {
        binding.vpFullSkin.offscreenPageLimit = skins?.size ?: 0
        binding.vpFullSkin.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                try {
                    val viewHolder = (binding.vpFullSkin[0] as RecyclerView).findViewHolderForAdapterPosition(position) as ChampionFullSkinAdapter.ChampionFullSkinViewHolder
                    changeSelectSkin(viewHolder.binding.imgChampionSkin.drawable, if (position == 0) null else skinAdapter.currentList[position].name)
                } catch (e: Exception) {
                    changeSelectSkin(null, null)
                }
            }
        })
    }
}