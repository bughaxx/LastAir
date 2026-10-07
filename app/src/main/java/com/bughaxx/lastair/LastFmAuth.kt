package com.bughaxx.lastair

import java.security.MessageDigest

object LastFmAuth {
    fun computeApiSig(params: Map<String, String>, secret: String): String {
        val sortedParams = params.entries.sortedBy { it.key }
        val toHash = sortedParams.joinToString("") { "${it.key}${it.value}" } + secret

        val md = MessageDigest.getInstance("MD5")
        val hashBytes = md.digest(toHash.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun getAuthUrl(apiKey: String, token: String): String {
        val encodedCallback = android.net.Uri.encode("lastair://callback")
        return "https://www.last.fm/api/auth/?api_key=$apiKey&token=$token&cb=$encodedCallback"

    }
}