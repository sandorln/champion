package com.sandorln.network.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.installations.FirebaseInstallations
import com.sandorln.network.model.FireStoreGame
import com.sandorln.network.util.getGameDocument
import com.sandorln.network.util.getUserId
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameService @Inject constructor(
    private val fireDB: FirebaseFirestore
) {
    suspend fun updateGameScore(fireStoreGame: FireStoreGame, score: Long) {
        val id = FirebaseInstallations.getInstance().getUserId()
        val data = mapOf("score" to score)

        fireDB
            .getGameDocument(fireStoreGame)
            .document(id)
            .set(data)
            .await()
    }

    suspend fun getInitialGameRank(
        fireStoreGame: FireStoreGame,
        currentScore: Long
    ): Int {
        val id = FirebaseInstallations.getInstance().getUserId()
        val rankListDocument = fireDB
            .getGameDocument(fireStoreGame)
            .whereGreaterThanOrEqualTo("score", currentScore)
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(30)
            .get()
            .await()
            .documents

        val rankIndex = rankListDocument
            .indexOfFirst { it.id == id }
            .takeIf { it >= 0 } ?: 30

        return rankIndex + 1
    }
}