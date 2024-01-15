package com.sandorln.champion.repository

import androidx.paging.Pager
import com.google.firebase.firestore.QuerySnapshot
import com.sandorln.model.ChampionBoard

interface BoardRepository {
    fun getChampionBoardPaging(championId: String): Pager<QuerySnapshot, com.sandorln.model.ChampionBoard>
}