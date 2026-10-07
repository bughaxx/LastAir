package com.bughaxx.lastair

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.bughaxx.lastair.RetrofitInstance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class FriendsViewModel : ViewModel() {

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun loadFriends(username: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = RetrofitInstance.api.getFriends(
                    user = username,
                    apiKey = BuildConfig.LASTFM_API_KEY
                )

                val friendsList = response.friends.user
                val friends = coroutineScope {
                    friendsList.map { friend ->
                        async {
                            val playcount = try {
                                RetrofitInstance.api.getUserInfo(
                                    user = friend.name,
                                    apiKey = BuildConfig.LASTFM_API_KEY
                                ).user.playcount.toIntOrNull() ?: 0
                            } catch (e: Exception) { 0 }
                            Friend(
                                name = friend.name,
                                imageUrl = friend.image.getOrNull(2)?.url ?: "",
                                playcount = playcount
                            )
                        }
                    }.awaitAll()
                }
                _friends.value = friends

            } catch (e: Exception) {
                println("Error with Last.fm : ${e.message}")
            }
            _isLoading.value = false
        }
    }

}