package com.bughaxx.lastair

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendsActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val cache = CacheRepository(application)
    private val gson = Gson()

    private val _tracks = MutableStateFlow<List<FriendTrack>>(emptyList())
    val tracks: StateFlow<List<FriendTrack>> = _tracks

    private val _myReactions = MutableStateFlow<Map<String, String>>(emptyMap())
    val myReactions: StateFlow<Map<String, String>> = _myReactions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private var currentPage = 1
    private var friends: List<LastFmFriend> = emptyList()

    private val repo = ReactionRepository()

    fun loadFriendsActivity(username: String) {
        currentPage = 1
        viewModelScope.launch {
            _isLoading.value = true

            cache.getFriendsFeed()?.let { json ->
                _tracks.value = gson.fromJson(json, object : TypeToken<List<FriendTrack>>() {}.type)
                _isLoading.value = false
            }

            try {
                val response = RetrofitInstance.api.getFriends(
                    user = username,
                    apiKey = BuildConfig.LASTFM_API_KEY
                )
                friends = response.friends.user
                val freshTracks = fetchPage(page = 1)
                _tracks.value = freshTracks
                cache.setFriendsFeed(gson.toJson(freshTracks))
            } catch (e: Exception) {
                println("loadFriendsActivity error: ${e.message}")
            }
            val reactions = repo.loadMyReactions(username)
            _myReactions.value = reactions

            _isLoading.value = false
        }
    }

    fun sendReaction(
        from: String,
        to: String,
        emoji: String,
        trackName: String,
        artistName: String,
        trackImage: String,
        friendTimestamp: Long
    ) {
        val docId = reactionDocId(from, to, trackName, artistName, friendTimestamp)
        viewModelScope.launch {
            repo.sendReaction(from, to, emoji, trackName, artistName, trackImage, friendTimestamp)
            _myReactions.update {
                it + (docId to emoji)
            }
        }
    }

    fun removeReaction(docId: String) {
        viewModelScope.launch {
            repo.removeReaction(docId)
            _myReactions.update { it - docId }

        }
    }

    fun loadMore() {
        if (_isLoadingMore.value || _isLoading.value || friends.isEmpty()) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            currentPage++
            try {
                val moreTracks = fetchPage(page = currentPage)
                _tracks.value = (_tracks.value + moreTracks).sortedByDescending { it.timestamp }
            } catch (e: Exception) {
                currentPage--
                println("loadMore error: ${e.message}")
            }
            _isLoadingMore.value = false
        }
    }


    private suspend fun fetchPage(page: Int): List<FriendTrack> {
        val allTracks = friends.flatMap { friend ->
            try {
                val response = RetrofitInstance.api.getRecentTracks(
                    user = friend.name,
                    apiKey = BuildConfig.LASTFM_API_KEY,
                    limit = 10,
                    page = page
                )
                response.recenttracks.track.mapNotNull { track ->
                    val isNowPlaying = track.attr?.nowplaying == "true"
                    val uts = track.date?.uts?.toLongOrNull()
                    if (!isNowPlaying && uts == null) return@mapNotNull null

                    FriendTrack(
                        name = track.name,
                        artist = track.artist.text,
                        coverUrl = ItunesHelper.getHighQualityCover(
                            track.artist.text, track.name, track.image.bestUrl()
                        ),
                        username = friend.name,
                        timestamp = uts ?: (System.currentTimeMillis() / 1000),
                        isNowPlaying = isNowPlaying
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
        return allTracks.sortedByDescending { it.timestamp }
    }
}