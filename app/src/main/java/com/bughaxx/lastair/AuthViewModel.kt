package com.bughaxx.lastair

import android.content.Context
import android.content.Intent
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    private var pendingLastFmToken: String? = null

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _userProfile = MutableStateFlow<UserProfile?>(null)

    private val _isProfileLoaded = MutableStateFlow(false)
    val isProfileLoaded: StateFlow<Boolean> = _isProfileLoaded
    val userProfile: StateFlow<UserProfile?> = _userProfile

    init {
        val existingUser = auth.currentUser
        if (existingUser != null) {
            viewModelScope.launch {
                val profile = userRepository.getUserProfile(existingUser.uid)
                android.util.Log.d("LastAir", "Profil chargé: ${profile?.lastfmUsername}")
                _userProfile.value = profile
                _isProfileLoaded.value = true

            }
        } else {
            _isProfileLoaded.value = true
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                // Credential Manager est le nouveau gestionnaire d'identité d'Android.
                // Il gère Google, passkeys, et d'autres méthodes de façon unifiée.
                val credentialManager = CredentialManager.create(context)

                // On décrit ce qu'on veut : un token Google ID
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(BuildConfig.FIREBASE_WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false) // affiche tous les comptes Google
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // Cette ligne ouvre la fenêtre système de sélection de compte.
                // "suspend" : on attend que l'utilisateur choisisse sans bloquer l'UI.
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                // On extrait le token Google de la réponse
                val googleIdToken = GoogleIdTokenCredential
                    .createFrom(credential.data)
                    .idToken

                // On l'échange contre une session Firebase
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                // Mise à jour de l'état : l'utilisateur est maintenant connecté
                _currentUser.value = authResult.user

                authResult.user?.let { user ->
                    val profile = userRepository.getUserProfile(user.uid)
                    _userProfile.value = profile
                    _isProfileLoaded.value = true
                }


            } catch (e: Exception) {
                _isProfileLoaded.value = true // ← et ça aussi en cas d'erreur
                android.util.Log.e("LastAir", "Erreur Sign-In: ${e.message}")
            }
        }
    }

    fun startLastFmAuth(context: Context) {
        viewModelScope.launch {
            try {
                val params = mapOf(
                    "method" to "auth.getToken",
                    "api_key" to BuildConfig.LASTFM_API_KEY
                )
                val apiSig = LastFmAuth.computeApiSig(params, BuildConfig.LASTFM_SECRET)
                val tokenResponse = RetrofitInstance.api.getToken(
                    apiKey = BuildConfig.LASTFM_API_KEY,
                    apiSig = apiSig
                )

                pendingLastFmToken = tokenResponse.token
                android.util.Log.d("LastAir", "Token sauvegardé: ${tokenResponse.token}")

                val authUrl = LastFmAuth.getAuthUrl(
                    apiKey = BuildConfig.LASTFM_API_KEY,
                    token = tokenResponse.token
                )
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(authUrl))
                context.startActivity(intent)
            } catch (e: Exception) {
                android.util.Log.e("LastAir", "Erreur startLastFmAuth: ${e.message}")
            }
        }
    }

    fun tryExchangePendingToken() {
        android.util.Log.d("LastAir", "tryExchangePendingToken - pendingToken: $pendingLastFmToken")
        val token = pendingLastFmToken ?: return
        pendingLastFmToken = null
        handleLastFmCallback(token)
    }

    fun handleLastFmCallback(token: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("LastAir", "Échange du token: $token")

                val params = mapOf(
                    "method" to "auth.getSession",
                    "api_key" to BuildConfig.LASTFM_API_KEY,
                    "token" to token
                )
                val apiSig = LastFmAuth.computeApiSig(params, BuildConfig.LASTFM_SECRET)

                val sessionResponse = RetrofitInstance.api.getSession(
                    apiKey = BuildConfig.LASTFM_API_KEY,
                    token = token,
                    apiSig = apiSig
                )
                android.util.Log.d("LastAir", "Session obtenue: ${sessionResponse.session.name}")

                val lastfmUsername = sessionResponse.session.name
                val lastfmSk = sessionResponse.session.key

                val user = _currentUser.value ?: return@launch
                val profile = UserProfile(
                    uid = user.uid,
                    email = user.email ?: "",
                    lastfmUsername = lastfmUsername,
                    lastfmSk = lastfmSk
                )
                userRepository.saveUserProfile(profile)
                _userProfile.value = profile
                android.util.Log.d("LastAir", "userProfile mis à jour: ${profile.lastfmUsername}")


            } catch (e: Exception) {
                android.util.Log.e("LastAir", "Erreur handleLastFmCallback: ${e.message}")
            }
        }
    }

    fun saveLastfmUsername(username: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val profile = UserProfile(
                uid = user.uid,
                email = user.email ?: "",
                lastfmUsername = username
            )
            userRepository.saveUserProfile(profile)
            _userProfile.value = profile
        }
    }


    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _userProfile.value = null
    }
}