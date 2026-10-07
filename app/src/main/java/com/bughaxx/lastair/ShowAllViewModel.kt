package com.bughaxx.lastair

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bughaxx.lastair.ui.theme.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MediaItem(
    val name: String,
    val subtitle: String, // artiste pour tracks/albums, playcount pour artistes
    val imageUrl: String
)

class ShowAllViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<MediaItem>>(value = emptyList())
    val items : StateFlow<List<MediaItem>> = _items

    private val _period = MutableStateFlow("overall")
    val period: StateFlow<String> = _period

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading



    fun setPeriod(newPeriod: String, username: String, type: String) {
        _period.value= newPeriod
        _items.value = emptyList()
        load(type, username, newPeriod)
    }


    fun load(type: String, username: String, period: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (type) {
                "tracks" -> {
                    try {
                        val response = RetrofitInstance.api.getTopTracks(
                            user = username,
                            apiKey = BuildConfig.LASTFM_API_KEY,
                            period = period
                        )
                        _items.value = response.toptracks.track.map { track ->
                            MediaItem(
                                name = track.name,
                                subtitle = track.playcount + " plays",
                                imageUrl = ItunesHelper.getHighQualityCover(
                                    track.artist.name,
                                    track.name,
                                    track.image.bestUrl()
                                )
                            )
                        }
                    } catch (e: Exception) {
                        println("Error with Top Artist : ${e.message}")
                    }

                }

                "albums" -> {
                    try {
                        val response = RetrofitInstance.api.getTopAlbums(
                            user = username,
                            apiKey = BuildConfig.LASTFM_API_KEY,
                            period = period
                        )
                        _items.value = response.topalbums.album.map { album ->
                            MediaItem(
                                name = album.name,
                                subtitle = album.artist.name,
                                imageUrl = ItunesHelper.getAlbumCover(
                                    album.artist.name,
                                    album.name,
                                    album.image.bestUrl()
                                )
                            )
                        }
                    } catch (e: Exception) {
                        println("Error with Top Albums : ${e.message}")
                    }
                }

                "artists" -> {
                    try {
                        val response = RetrofitInstance.api.getTopArtists(
                            user = username,
                            apiKey = BuildConfig.LASTFM_API_KEY,
                            period = period
                        )
                        _items.value = response.topartists.artist.map { artist ->
                            MediaItem(
                                name = artist.name,
                                subtitle = artist.playcount + " plays",
                                imageUrl = ItunesHelper.getArtistImage(
                                    artist.name,
                                    artist.image.bestUrl()
                                )
                            )
                        }
                    } catch (e: Exception) {
                        println("Error with Top Artist : ${e.message}")
                    }
                }
            }
            _isLoading.value = false
        }


    }

}