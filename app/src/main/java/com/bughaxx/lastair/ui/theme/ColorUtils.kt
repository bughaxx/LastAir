package com.bughaxx.lastair.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun artistToGradient(artist: String): Pair<Color, Color> {
    val hash = artist.trim().lowercase().hashCode()
    val hue = ((hash % 360) + 360) % 360f
    val color1 = Color.hsl(hue, 0.30f, 0.32f)
    val color2 = Color.hsl((hue + 25f) % 360f, 0.40f, 0.22f)
    return color1 to color2
}

@Composable
fun GenerativeCover(
    artist: String,
    modifier: Modifier = Modifier
) {
    val (color1, color2) = remember(artist) { artistToGradient(artist) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(color1, color2))),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.25f),
            modifier = Modifier.size(48.dp)
        )
    }
}