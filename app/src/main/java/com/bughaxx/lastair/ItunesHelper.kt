package com.bughaxx.lastair


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

object ItunesHelper {

    private const val LASTFM_PLACEHOLDER = "2a96cbd8b46e442fc41c2b86b821562f"

    private fun isPlaceholder(url: String) =
        url.isBlank() || url.contains(LASTFM_PLACEHOLDER)

    suspend fun getHighQualityCover(
        artist: String,
        track: String,
        lastFmUrl: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val term = URLEncoder.encode("$artist $track", "UTF-8")
            val raw = URL(
                "https://itunes.apple.com/search?term=$term&media=music&entity=song&limit=1"
            ).readText()
            val results = JSONObject(raw).getJSONArray("results")
            if (results.length() > 0) {
                return@withContext results.getJSONObject(0)
                    .getString("artworkUrl100")
                    .replace("100x100bb", "1200x1200bb")
            }
        } catch (e: Exception) { /* fall through */ }

        // fallback Last.fm uniquement si iTunes échoue
        if (!isPlaceholder(lastFmUrl)) lastFmUrl else ""
    }

    suspend fun getArtistImage(artist: String, lastFmUrl: String): String =
        withContext(Dispatchers.IO) {
            try {
                val term = URLEncoder.encode(artist, "UTF-8")
                val raw = URL(
                    "https://itunes.apple.com/search?term=$term&media=music&entity=song&limit=1"
                ).readText()
                val results = JSONObject(raw).getJSONArray("results")
                if (results.length() > 0) {
                    val url = results.getJSONObject(0)
                        .optString("artworkUrl100", "")
                        .replace("100x100bb", "1200x1200bb")
                    if (url.isNotBlank()) return@withContext url
                }
            } catch (e: Exception) { }
            if (!isPlaceholder(lastFmUrl)) lastFmUrl else ""
        }

    suspend fun getAlbumCover(artist: String, album: String, lastFmUrl: String): String =
        withContext(Dispatchers.IO) {
            try {
                val term = URLEncoder.encode("$artist $album", "UTF-8")
                val raw = URL(
                    "https://itunes.apple.com/search?term=$term&media=music&entity=album&limit=1"
                ).readText()
                val results = JSONObject(raw).getJSONArray("results")
                if (results.length() > 0) {
                    val url = results.getJSONObject(0)
                        .optString("artworkUrl100", "")
                        .replace("100x100bb", "1200x1200bb")
                    if (url.isNotBlank()) return@withContext url
                }
            } catch (e: Exception) { }
            if (!isPlaceholder(lastFmUrl)) lastFmUrl else ""
        }
}