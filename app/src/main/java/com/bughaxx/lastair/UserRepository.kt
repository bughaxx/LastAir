package com.bughaxx.lastair

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import kotlinx.coroutines.tasks.await

data class UserProfile(
    var uid: String = "",
    var email: String = "",
    @PropertyName("lastfm_username")
    var lastfmUsername: String? = null,
    @PropertyName("lastfm_sk")
    var lastfmSk: String? = null
)

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = db.collection("users").document(uid).get().await()
        return if (doc.exists()) doc.toObject(UserProfile::class.java) else null
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        db.collection("users")
            .document(profile.uid)
            .set(profile)
            .await()
    }
}