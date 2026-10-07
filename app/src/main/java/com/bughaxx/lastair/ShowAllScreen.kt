package com.bughaxx.lastair

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bughaxx.lastair.ui.theme.GenerativeCover
import com.bughaxx.lastair.ui.theme.robotoFlexVariable


@OptIn(ExperimentalTextApi::class)
@Composable
fun ShowAllScreen(
    type: String,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val viewModel: ShowAllViewModel = viewModel()
    val items by viewModel.items.collectAsState()
    val selectedPeriod by viewModel.period.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(userProfile?.lastfmUsername) {
        userProfile?.lastfmUsername?.let { username ->
            viewModel.load(type, username, selectedPeriod)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 7.dp),
                    text = "Top $type",
                    style = TextStyle(
                        fontFamily = robotoFlexVariable,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (1).sp,
                        fontStyle = FontStyle.Italic
                    )
                )
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 6.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {


            LazyVerticalGrid(GridCells.Fixed(2)) {


                item(span = { GridItemSpan(2) }) {

                    val periods = listOf(
                        "overall" to "All time",
                        "12month" to "1 year",
                        "6month" to "6 months",
                        "3month" to "3 months",
                        "1month" to "1 month",
                        "7day" to "1 week",
                    )




                    LazyRow(
                    ) {
                        items(periods) { (apiValue, label) ->
                            FilterChip(
                                selected = selectedPeriod == apiValue,
                                onClick = {
                                    viewModel.setPeriod(
                                        apiValue,
                                        userProfile?.lastfmUsername ?: "",
                                        type
                                    )
                                },
                                label = { Text(label) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }


                item(span = { GridItemSpan(2) }) {

                }
                    if (isLoading && items.isEmpty()) {
                        items(10) {
                            Surface(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .aspectRatio(1f),
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                val transition = rememberInfiniteTransition(label = "shimmer")
                                val translateAnim by transition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 1000f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(
                                            durationMillis = 1000,
                                            easing = LinearEasing
                                        )
                                    ),
                                    label = "translate"
                                )

                                val brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.LightGray.copy(alpha = 0.6f),
                                        Color.LightGray.copy(alpha = 0.3f),
                                        Color.LightGray.copy(alpha = 0.6f),
                                    ),
                                    start = Offset(translateAnim - 200f, 0f),
                                    end = Offset(translateAnim, 200f)
                                )

                                Box(
                                    modifier = Modifier
                                        .background(brush)
                                        .fillMaxWidth()
                                        .height(120.dp)
                                )


                            }
                        }
                    }







                if (!isLoading && items.isNotEmpty()) {
                    itemsIndexed(items) { index, mediaItem ->
                        Surface(
                            modifier = Modifier.padding(3.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            MediaItemCard(
                                mediaItem = mediaItem,
                                rank = index + 1,
                                modifier = Modifier.aspectRatio(1f),

                                )
                        }
                    }
                }

                if (!isLoading && items.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(17.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                            ) {
                                Text(
                                    text = "Nothing to show here yet, go listen to music !",
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }


        }
    }
}

@Composable
fun MediaItemCard(
    mediaItem: MediaItem,
    rank: Int,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier) {

        if (mediaItem.imageUrl.isBlank()) {
            GenerativeCover(
                artist = mediaItem.name,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = mediaItem.imageUrl,
                contentDescription = mediaItem.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to Color.Transparent,
                        0.75f to Color.Black.copy(0.4f),
                        1f to Color.Black.copy(0.92f)
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                rank.toString(),
                color = MaterialTheme.colorScheme.inverseOnSurface,
                style = TextStyle(
                    fontFamily = robotoFlexVariable,
                    fontSize = 40.sp
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {


                    Text(
                        mediaItem.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        mediaItem.subtitle,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall
                    )


                }
            }
        }

    }
}