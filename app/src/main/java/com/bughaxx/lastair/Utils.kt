package com.bughaxx.lastair

fun formatTimestamp(unixSeconds: Long): String {
    val diff = System.currentTimeMillis() / 1000 - unixSeconds
    return when {
        diff < 60 -> "just now"
        diff < 3600 -> "${diff / 60}m ago"
        diff < 86400 -> "${diff / 3600}h ago"
        diff < 86400 * 7 -> "${diff / 86400}d ago"
        else -> "${diff / (86400 * 7)}w ago"
    }
}