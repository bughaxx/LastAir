package com.bughaxx.lastair

import com.google.gson.annotations.SerializedName

data class RecentTracksResponse(
    val recenttracks: RecentTracks
)

data class RecentTracks(
    val track: List<LastFmTrack>
)

data class TopAlbums(
    val album: List<LastFmAlbum>
)

data class TopArtists(
    val artist: List<LastFmTopArtist>
)

data class LastFmAlbumArtist(
    val name: String
)

data class UserInfoResponse(val user: LastFmUserInfo)
data class LastFmUserInfo(val playcount: String)


data class LastFmFriendsResponse(
    val friends: LastFmFriendsContainer
)

data class LastFmFriendsContainer(
    val user: List<LastFmFriend>
)

data class LastFmFriend(
    val name: String,
    val playcount: String,
    val image: List<LastFmImage>,
)

data class FriendTrack(
    val name: String,
    val artist: String,
    val coverUrl: String,
    val username: String,
    val timestamp: Long,
    val isNowPlaying: Boolean = false,
    val myReaction: String? = null
)

/** Inbox reaction received from Firestore */
data class InboxItem(
    val from: String,
    val emoji: String,
    val trackName: String,
    val artistName: String,
    val trackImage: String,
    val timestamp: Long
)

data class LastFmTrack(
    val name: String,
    val artist: LastFmTrackArtist,
    val image: List<LastFmImage>,
    val date: LastFmDate?,
    @SerializedName("@attr") val attr: LastFmTrackAttr? = null
)

data class LastFmTrackAttr(
    val nowplaying: String? = null
)

data class LastFmDate(
    val uts: String
)


data class TopAlbumsResponse(
    val topalbums: TopAlbums
)

data class TopTracks(
    val track: List<LastFmTracks>
)

data class LastFmTracks(
    val name: String,
    val playcount: String,
    val artist: LastFmAlbumArtist,
    val image: List<LastFmImage>
)
data class TopTracksResponse(
    val toptracks: TopTracks
)

data class LastFmAlbum(
    val name: String,
    val playcount: String,
    val artist: LastFmAlbumArtist,
    val image: List<LastFmImage>
)


data class TopArtistsResponse(
    val topartists: TopArtists
)

data class LastFmTopArtist(
    val name: String,
    val playcount: String,
    val image: List<LastFmImage>
)


data class LastFmTrackArtist(
    @SerializedName("#text") val text: String
)

data class LastFmImage(
    @SerializedName("#text") val url: String,
    val size: String
)


data class TokenResponse(val token: String)

data class SessionResponse(val session: LastFmSession)

data class LastFmSession(
    val name: String,
    val key: String,
)

fun List<LastFmImage>.bestUrl(): String {
    val priority = listOf("mega", "extralarge", "large")

    return priority.firstNotNullOfOrNull { size ->
        firstOrNull { it.size == size }?.url
            ?.takeIf { it.isNotBlank() }
    } ?: ""

}

val LastFmTrack.coverUrl: String get() = image.bestUrl()

val LastFmAlbum.coverUrl: String get() = image.bestUrl()

val LastFmFriend.coverUrl: String get() = image.bestUrl()

val LastFmTopArtist.coverUrl: String get() = image.bestUrl()
