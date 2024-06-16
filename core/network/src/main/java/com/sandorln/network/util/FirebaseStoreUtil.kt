package com.sandorln.network.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.sandorln.network.BuildConfig
import com.sandorln.network.model.FireStoreDocument
import com.sandorln.network.model.FireStoreGame
import kotlinx.coroutines.tasks.await

private val BASE_COLLECTION_NAME: String = if (BuildConfig.DEBUG) "dev" else "release"
fun FirebaseFirestore.getLolDocument(fireStoreDocument: FireStoreDocument) =
    collection(BASE_COLLECTION_NAME)
        .document(fireStoreDocument.name.lowercase())

fun FirebaseFirestore.getGameDocument(fireStoreGame: FireStoreGame) =
    collection(BASE_COLLECTION_NAME)
        .document(FireStoreDocument.GAME.name.lowercase())
        .collection(fireStoreGame.name.lowercase())

suspend fun FirebaseInstallations.getUserId(): String = runCatching {
    FirebaseInstallations.getInstance().id.await()
}.getOrNull() ?: ""