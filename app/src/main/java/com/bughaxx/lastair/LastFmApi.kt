package com.bughaxx.lastair

import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmApi {

    @GET("2.0/")
    suspend fun getToken(
        @Query("method") method: String = "auth.getToken",
        @Query("api_key") apiKey: String,
        @Query("api_sig") apiSig: String,
        @Query("format") format: String = "json"
    ): TokenResponse

    @GET("2.0/")
    suspend fun getSession(
        @Query("method") method: String = "auth.getSession",
        @Query("api_key") apiKey: String,
        @Query("token") token: String,
        @Query("api_sig") apiSig: String,
        @Query("format") format: String = "json"
    ): SessionResponse

    @GET("2.0/")
    suspend fun getRecentTracks(
        @Query("method") method: String = "user.getrecenttracks",
        @Query("user") user: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1
    ): RecentTracksResponse


    @GET("2.0/")
    suspend fun getTopAlbums(
        @Query("method") method: String = "user.gettopalbums",
        @Query("user") user: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 10,
        @Query("period") period: String = "overall"
    ): TopAlbumsResponse

    @GET("2.0/")
    suspend fun getTopArtists(
        @Query("method") method: String = "user.gettopartists",
        @Query("user") user: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 50,
        @Query("period") period: String = "overall"
    ): TopArtistsResponse

    @GET("2.0/")
    suspend fun getUserInfo(
        @Query("method") method: String = "user.getinfo",
        @Query("user") user: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json"
    ): UserInfoResponse


    @GET("2.0/")
    suspend fun getFriends(
        @Query("method") method: String = "user.getfriends",
        @Query("user") user: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 50
    ): LastFmFriendsResponse

    @GET("2.0/")
    suspend fun getTopTracks(
        @Query("method") method: String = "user.gettoptracks",
        @Query("user") user: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 50,
        @Query("period") period: String = "overall"
    ): TopTracksResponse


}