package com.bughaxx.lastair

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class InboxViewModel(application: Application) : AndroidViewModel(application) {

    private val cache = CacheRepository(application)
    private val gson  = Gson()

    private val _inbox     = MutableStateFlow<List<InboxItem>>(emptyList())
    val inbox: StateFlow<List<InboxItem>> = _inbox

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var lastUsername = ""

    // ── Called from LaunchedEffect on first load ───────────────────────────

    fun loadInbox(username: String) {
        lastUsername = username
        viewModelScope.launch {
            _isLoading.value = true

            // Show cache immediately
            cache.getInbox()?.let { json ->
                _inbox.value = gson.fromJson(json, object : TypeToken<List<InboxItem>>() {}.type)
                _isLoading.value = false
            }

            fetchFromNetwork(username)
            _isLoading.value = false
        }
    }

    // ── Called by pull-to-refresh ──────────────────────────────────────────

    fun refresh() {
        if (lastUsername.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            fetchFromNetwork(lastUsername)
            _isLoading.value = false
        }
    }

    // ── Private ────────────────────────────────────────────────────────────

    private suspend fun fetchFromNetwork(username: String) {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("reactions")
                .whereEqualTo("to", username)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                val from        = doc.getString("from")        ?: return@mapNotNull null
                val emoji       = doc.getString("emoji")       ?: ""
                val trackName   = doc.getString("trackName")   ?: ""
                val artistName  = doc.getString("artistName")  ?: ""
                val trackImage  = doc.getString("trackImage")  ?: ""
                val timestamp   = doc.getTimestamp("timestamp")?.seconds ?: 0L
                InboxItem(
                    from       = from,
                    emoji      = emoji,
                    trackName  = trackName,
                    artistName = artistName,
                    trackImage = trackImage,
                    timestamp  = timestamp
                )
            }

            _inbox.value = items
            cache.setInbox(gson.toJson(items))
        } catch (e: Exception) {
            println("Inbox fetch error: ${e.message}")
        }
    }
}