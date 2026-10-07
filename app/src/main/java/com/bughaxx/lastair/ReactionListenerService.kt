package com.bughaxx.lastair

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ReactionListenerService : Service() {

    private lateinit var db: FirebaseFirestore
    private var listenerRegistration: ListenerRegistration? = null

    private var startTimestamp: Timestamp = Timestamp.now()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val username = intent?.getStringExtra("username") ?: return START_NOT_STICKY

        listenerRegistration?.remove()
        listenerRegistration = null
        startTimestamp = Timestamp.now()

        db = FirebaseFirestore.getInstance()

        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, "service_channel")
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", packageName, null)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "service_channel")
            .setContentTitle("Notification Service is Running")
            .setContentText("Hide this notification by clicking on it")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .build()

        startForeground(1, notification)


        startListening(username)

        return START_STICKY  // Android redémarre le service s'il est tué
    }

    private fun startListening(username: String) {
        listenerRegistration = db.collection("reactions")
            .whereEqualTo("to", username)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) return@addSnapshotListener

                for (change in snapshot.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        val emoji = change.document.getString("emoji") ?: continue
                        val from = change.document.getString("from") ?: continue
                        val trackName = change.document.getString("trackName") ?: continue
                        val reactionTime = change.document.getTimestamp("timestamp") ?: continue
                        if (reactionTime <= startTimestamp) continue
                        showReactionNotification(from, emoji, trackName)
                    }
                }
            }
    }

    private fun showReactionNotification(from: String, emoji: String, trackName: String) {


        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("route", "Inbox")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "reactions_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(from)
            .setContentIntent(pendingIntent)
            .setContentText("reacted $emoji to $trackName • 👀")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(System.currentTimeMillis().toInt(), notification)

    }


override fun onDestroy() {
    listenerRegistration?.remove()
    super.onDestroy()
}

override fun onBind(intent: Intent?) = null
}