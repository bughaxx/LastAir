package com.bughaxx.lastair

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class ReactionRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun sendReaction(
        from: String,
        to: String,
        emoji: String,
        trackName: String,
        artistName: String,
        trackImage: String,
        friendTimestamp: Long
    ) {
        val reaction = hashMapOf(
            "from" to from,
            "to" to to,
            "emoji" to emoji,
            "trackName" to trackName,
            "artistName" to artistName,
            "trackImage" to trackImage,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        db.collection("reactions")
            .document(reactionDocId(from, to, trackName, artistName, friendTimestamp)).set(reaction)
            .await()
    }

    suspend fun removeReaction(docId: String) {
        db.collection("reactions").document(docId).delete().await()
    }

    suspend fun loadMyReactions(from: String): Map<String, String> {
        val result = db.collection(("reactions"))
            .whereEqualTo("from", from)
            .get()
            .await()
        return result.documents.associate { doc ->
            doc.id to (doc.getString("emoji") ?: "")
        }

    }
}
