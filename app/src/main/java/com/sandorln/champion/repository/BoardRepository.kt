package com.sandorln.champion.repository

import androidx.paging.Pager
import com.google.firebase.firestore.QuerySnapshot
import com.sandorln.champion.model.ChampionBoard

interface BoardRepository {
    fun getChampionBoardPagingFlow(championId: String): Pager<QuerySnapshot, ChampionBoard>
}