package com.bughaxx.lastair

import java.security.MessageDigest

fun reactionDocId(
    from: String,
    to: String,
    trackName: String,
    artistName: String,
    friendTimestamp: Long
): String {
    val raw = "$from|$to|$trackName|$artistName|$friendTimestamp"
    return MessageDigest.getInstance("SHA-256")
        .digest(raw.toByteArray())
        .joinToString("") { "%02x".format(it) }
}
