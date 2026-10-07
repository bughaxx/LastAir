package com.bughaxx.lastair

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "lastair_cache")

class CacheRepository(private val context: Context) {

    companion object {
        private const val TTL_MS = 5 * 60 * 1000L // 5 minutes

        private const val CACHE_VERSION = 4

        private const val KEY_VERSION = "cache_version"

        val KEY_HOME_TRACKS     = stringPreferencesKey("home_tracks")
        val KEY_HOME_TRACKS_TS  = longPreferencesKey("home_tracks_ts")

        val KEY_TOP_ALBUMS      = stringPreferencesKey("top_albums")
        val KEY_TOP_ALBUMS_TS   = longPreferencesKey("top_albums_ts")

        val KEY_TOP_ARTISTS     = stringPreferencesKey("top_artists")
        val KEY_TOP_ARTISTS_TS  = longPreferencesKey("top_artists_ts")

        val KEY_FRIENDS_FEED    = stringPreferencesKey("friends_feed_v$CACHE_VERSION")
        val KEY_FRIENDS_FEED_TS = longPreferencesKey("friends_feed_ts")

        val KEY_INBOX           = stringPreferencesKey("inbox")
        val KEY_INBOX_TS        = longPreferencesKey("inbox_ts")
    }

    private fun isFresh(ts: Long) = System.currentTimeMillis() - ts < TTL_MS

    private suspend fun get(key: Preferences.Key<String>, tsKey: Preferences.Key<Long>): String? {
        val prefs = context.dataStore.data.first()
        val ts = prefs[tsKey] ?: 0L
        return if (isFresh(ts)) prefs[key] else null
    }

    private suspend fun set(key: Preferences.Key<String>, tsKey: Preferences.Key<Long>, value: String) {
        context.dataStore.edit { prefs ->
            prefs[key] = value
            prefs[tsKey] = System.currentTimeMillis()
        }
    }

    suspend fun getHomeTracks()  = get(KEY_HOME_TRACKS,  KEY_HOME_TRACKS_TS)
    suspend fun setHomeTracks(v: String)  = set(KEY_HOME_TRACKS,  KEY_HOME_TRACKS_TS, v)

    suspend fun getTopAlbums()   = get(KEY_TOP_ALBUMS,   KEY_TOP_ALBUMS_TS)
    suspend fun setTopAlbums(v: String)   = set(KEY_TOP_ALBUMS,   KEY_TOP_ALBUMS_TS, v)

    suspend fun getTopArtists()  = get(KEY_TOP_ARTISTS,  KEY_TOP_ARTISTS_TS)
    suspend fun setTopArtists(v: String)  = set(KEY_TOP_ARTISTS,  KEY_TOP_ARTISTS_TS, v)

    suspend fun getFriendsFeed() = get(KEY_FRIENDS_FEED, KEY_FRIENDS_FEED_TS)
    suspend fun setFriendsFeed(v: String) = set(KEY_FRIENDS_FEED, KEY_FRIENDS_FEED_TS, v)

    suspend fun getInbox()       = get(KEY_INBOX,        KEY_INBOX_TS)
    suspend fun setInbox(v: String)       = set(KEY_INBOX,        KEY_INBOX_TS, v)
}