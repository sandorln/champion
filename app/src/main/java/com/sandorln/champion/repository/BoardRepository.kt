package com.sandorln.champion.repository

import androidx.paging.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sandorln.champion.model.ChampionBoard

class BoardRepository {
    fun getChampionBoardPagingFlow(championId: String): Pager<QuerySnapshot, ChampionBoard> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        initialKey = null,
        pagingSourceFactory = { ChampionBoardPagingSource(championId) }
    )

    class ChampionBoardPagingSource(private val championId: String) : PagingSource<QuerySnapshot, ChampionBoard>() {
        private val db = FirebaseFirestore.getInstance()

        override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, ChampionBoard> = try {
            val pageSize = params.loadSize.toLong()

            db.collection(championId).limit(pageSize).get().addOnCompleteListener {

            }
            val currentPage = mutableListOf<ChampionBoard>()
            val nextPage = null

            LoadResult.Page(
                data = currentPage,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

        override fun getRefreshKey(state: PagingState<QuerySnapshot, ChampionBoard>): QuerySnapshot? = null
    }
}