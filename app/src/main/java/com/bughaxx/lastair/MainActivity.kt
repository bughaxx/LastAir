package com.bughaxx.lastair

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bughaxx.lastair.ui.theme.AppTheme
import okhttp3.internal.http2.Settings

class MainActivity : ComponentActivity() {
    private val deepLinkUri = mutableStateOf<android.net.Uri?>(null)
    private lateinit var authViewModel: AuthViewModel

    private val pendingRoute = mutableStateOf<String?>(null)





    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Silenced channel for foreground service

            val serviceChannel = NotificationChannel(
                "service_channel",
                "Background Service",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Silent notification to keep the service alive"
                setShowBadge(false)
            }

            // Reaction channel with vibrations

            val reactionsChannel = NotificationChannel(
                "reactions_channel",
                "Reactions",
                NotificationManager.IMPORTANCE_DEFAULT

            ).apply {
                description = "Notifications when friends react to your tracks"
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
            manager.createNotificationChannel(reactionsChannel)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        android.util.Log.d("LastAir", "onCreate - intent.data: ${intent.data}")
        deepLinkUri.value = intent?.data
        enableEdgeToEdge()
        intent.data?.let { deepLinkUri.value = it }
        authViewModel = androidx.lifecycle.ViewModelProvider(this)[AuthViewModel::class.java]
        createNotificationChannel()

        pendingRoute.value = intent?.getStringExtra("route")


        intent?.getStringExtra("route")?.let { route ->

        }



        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    AppNavigation(
                        authViewModel = authViewModel,
                        deepLinkUri = deepLinkUri.value,
                        pendingRoute = pendingRoute.value,
                        onRouteConsumed = { pendingRoute.value = null }
                    )
                }
            }
        }
    }



    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingRoute.value = intent.getStringExtra("route")
        android.util.Log.d("LastAir", "onNewIntent - intent.data: ${intent.data}")
        android.util.Log.d("LastAir", "onNewIntent - intent.data: ${intent.data}")
        deepLinkUri.value = intent.data
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.d("LastAir", "onResume appelé")
        if (::authViewModel.isInitialized) {
            authViewModel.tryExchangePendingToken()
        }

    }
}

