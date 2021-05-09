package com.sandorln.champion.view.adapter.listener

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.sandorln.champion.util.getSnapPosition

class SnapScrollListener(private val snapHelper: SnapHelper, val snapChangeListener: (snapPosition: Int) -> Unit) : RecyclerView.OnScrollListener() {
    var snapPosition = RecyclerView.NO_POSITION

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE)
            checkChangeSnapPosition(recyclerView)
    }

//    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//        checkChangeSnapPosition(recyclerView)
//    }

    private fun checkChangeSnapPosition(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChange = this.snapPosition != snapPosition
        if (snapPositionChange) {
            snapChangeListener(snapPosition)
            this.snapPosition = snapPosition
        }
    }
}