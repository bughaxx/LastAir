package com.bughaxx.lastair

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bughaxx.lastair.ui.theme.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Period

class HomeViewModel : ViewModel() {
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    private val _topAlbums = MutableStateFlow<List<Track>>(emptyList())


    val topAlbums: StateFlow<List<Track>> = _topAlbums
    private val _topArtists = MutableStateFlow<List<Track>>(emptyList())
    val topArtists: StateFlow<List<Track>> = _topArtists

    val tracks: StateFlow<List<Track>> = _tracks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun loadTracks(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getRecentTracks(
                    user = username,
                    apiKey = BuildConfig.LASTFM_API_KEY
                )
                _tracks.value = response.recenttracks.track.mapNotNull { track ->
                    Track(
                        name = track.name,
                        artist = track.artist.text,
                        coverUrl = ItunesHelper.getHighQualityCover(
                            track.artist.text,
                            track.name,
                            track.image.bestUrl()
                        )
                    )
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun loadTopAlbums(username: String, period: String = "overall") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getTopAlbums(
                    user = username,
                    apiKey = BuildConfig.LASTFM_API_KEY,
                    period = period
                )
                _topAlbums.value = response.topalbums.album.map { album ->
                    Track(
                        name = album.name,
                        artist = album.artist.name,
                        coverUrl = ItunesHelper.getAlbumCover(
                            album.artist.name,
                            album.name,
                            album.image.bestUrl()
                        )
                    )
                }
            } catch (e: Exception) {
                println("Error with Top Albums : ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun loadTopArtists(username: String, period: String = "overall") {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTopArtists(
                    user = username,
                    apiKey = BuildConfig.LASTFM_API_KEY,
                    period = period
                )
                _topArtists.value = response.topartists.artist.map { artist ->
                    Track(
                        name = artist.name,
                        artist = "", // un artiste n'a pas de sous-artiste
                        coverUrl = ItunesHelper.getArtistImage(
                            artist.name,
                            artist.image.bestUrl()
                        )
                    )
                }
            } catch (e: Exception) {
                println("Erreur top artists: ${e.message}")
            }
        }
    }
}