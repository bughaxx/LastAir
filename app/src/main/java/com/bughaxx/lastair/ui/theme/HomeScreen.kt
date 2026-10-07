package com.bughaxx.lastair.ui.theme

import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bughaxx.lastair.Account
import com.bughaxx.lastair.AuthViewModel
import com.bughaxx.lastair.FriendsActivityViewModel
import com.bughaxx.lastair.HomeViewModel
import com.bughaxx.lastair.Loading
import com.bughaxx.lastair.R
import com.bughaxx.lastair.ReactionListenerService
import com.bughaxx.lastair.ReactionRepository
import com.bughaxx.lastair.Routes
import com.bughaxx.lastair.formatTimestamp
import com.bughaxx.lastair.reactionDocId
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@OptIn(ExperimentalTextApi::class)
val robotoFlexVariable = FontFamily(
    Font(
        resId = R.font.roboto_flex, variationSettings = FontVariation.Settings(
            FontVariation.width(200f),
            FontVariation.weight(900),
        )
    )
)

data class Track(
    val name: String, val artist: String, val coverUrl: String, val isNowPlaying: Boolean = false
)


@Composable
fun MusicCard(
    trackName: String,
    artistName: String,
    coverUrl: String,
    modifier: Modifier = Modifier,
    isNowPlaying: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f, label = "card_scale"
    )
    Box(modifier = modifier) {

        if (coverUrl.isBlank()) {
            GenerativeCover(
                artist = artistName,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = coverUrl,
                contentDescription = trackName,
                modifier = Modifier.fillMaxSize(),
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
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        })
                }
                .graphicsLayer(scaleX = scale, scaleY = scale),
        )

        if (isNowPlaying) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding((10.dp)),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
            ) {

                Text(
                    text = "Now playing",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(trackName, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(
                artistName,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TrackCardShowAll(
    trackName: String,
    artistName: String,
    username: String = "",
    coverUrl: String,
    timestamp: Long = 0L,
    isNowPlaying: Boolean = false,
    fromUsername: String = "",
    isPickerOpen: Boolean = false,
    onPickerOpen: () -> Unit = {},
    onPickerClose: () -> Unit = {},
    myReaction: String?,
    onSendReaction: (String) -> Unit,
    onRemoveReaction: () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f, label = "card_scale"
    )

    val scope = rememberCoroutineScope()
    val repo = remember { ReactionRepository() }


    Box {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }, onDoubleTap = {
                        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        onSendReaction("❤️")
                    }, onLongPress = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPickerOpen()
                    })
                }
                .graphicsLayer(scaleX = scale, scaleY = scale), shape = RoundedCornerShape(24.dp)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .padding(end = 64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    if (coverUrl.isBlank()) {
                        GenerativeCover(
                            artist = artistName,
                            modifier = Modifier
                                .size(75.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        AsyncImage(
                            model = coverUrl,
                            contentDescription = "$trackName cover",
                            modifier = Modifier
                                .size(75.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = trackName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = Bold,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            if (isNowPlaying) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Now Playing",
                                        modifier = Modifier.padding(
                                            horizontal = 6.dp,
                                            vertical = 2.dp
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                }
                            }
                        }

                        Text(
                            text = artistName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                        ) {
                            Text(
                                text = username,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp, vertical = 2.dp
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                maxLines = 1
                            )
                        }
                    }

                    if (timestamp > 0L) {
                        Text(
                            text = formatTimestamp(timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )

                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                ) {
                    IconButton(
                        onClick = onPickerOpen,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddReaction,
                            contentDescription = "React",
                        )
                    }

                }

            }
        }

        var isVisible by remember { mutableStateOf(true) }

        LaunchedEffect(myReaction) {
            if (myReaction != null) isVisible = true
        }

        if (myReaction != null && isVisible) {

            val scale = remember { Animatable(0.6f) }

            LaunchedEffect(myReaction) {
                if (myReaction != null) isVisible = true
                haptics.performHapticFeedback(HapticFeedbackType.ToggleOn)
                scale.snapTo(0.6f)
                scale.animateTo(
                    1.15f,
                    animationSpec = tween(120, easing = FastOutLinearInEasing)
                )
                scale.animateTo(
                    1f,
                    animationSpec = spring(
                        dampingRatio = 0.5f,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }

            Surface(

                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 15.dp, bottom = 4.dp)
                    .zIndex(1f)
                    .scale(scale.value)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {

                            haptics.performHapticFeedback(HapticFeedbackType.Reject)

                            scope.launch {
                                scale.animateTo(
                                    0.85f,
                                    animationSpec = tween(80)
                                )
                                scale.animateTo(
                                    0f, animationSpec = tween(120)
                                )
                                isVisible = false
                                onRemoveReaction()
                            }
                        })
                    },
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = myReaction,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(5.dp)
                        .rotate(-20f)
                )
            }
        }


        val pickerScale = remember { Animatable(0.6f) }
        val scope = rememberCoroutineScope()


        if (isPickerOpen) {


            LaunchedEffect(isPickerOpen) {
                pickerScale.snapTo(0.6f)
                pickerScale.animateTo(
                    1.15f,
                    animationSpec = tween(120, easing = FastOutLinearInEasing)
                )
                pickerScale.animateTo(
                    1f,
                    animationSpec = spring(
                        dampingRatio = 0.5f,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            fun closeWithAnim() {
                scope.launch {
                    pickerScale.animateTo(0.9f, tween(80))
                    pickerScale.animateTo(0f, tween(120))
                    onPickerClose()
                }
            }

            Box(
                Modifier
                    .matchParentSize()
                    .clickable {
                        closeWithAnim()
                    })
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .wrapContentSize(unbounded = true)
                    .padding(end = 10.dp)
                    .scale(pickerScale.value),
                shape = RoundedCornerShape(50),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                    listOf("❤️", "🤣", "😫", "👍", "‼️").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable {
                                    onSendReaction(emoji)
                                    closeWithAnim()
                                }
                        )
                    }
                }
            }


        }


    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    navController: NavController
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    val friendsVM: FriendsActivityViewModel = viewModel()
    val tracks by homeViewModel.tracks.collectAsState()
    val isLoading by friendsVM.isLoading.collectAsState()
    val topArtists by homeViewModel.topArtists.collectAsState()
    val topAlbums by homeViewModel.topAlbums.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val friendsTracks by friendsVM.tracks.collectAsState()
    val context = LocalContext.current
    val isFriendsLoading by friendsVM.isLoading.collectAsState()
    val myReactions by friendsVM.myReactions.collectAsState()
    val isLoadingMore by friendsVM.isLoadingMore.collectAsState()
    val friendsListState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible =
                friendsListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= friendsTracks.size - 4 && friendsTracks.isNotEmpty()
        }
    }
    var openPickerKey by remember { mutableStateOf<String?>(null) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val accountImageUrl = currentUser?.photoUrl?.toString()
    val isLoggedIn = currentUser != null
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* résultat ignoré pour l'instant */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(userProfile?.lastfmUsername) {
        userProfile?.lastfmUsername?.let { username ->
            homeViewModel.loadTracks(username)
            homeViewModel.loadTopAlbums(username)
            homeViewModel.loadTopArtists(username)
            friendsVM.loadFriendsActivity(username)

            val intent = Intent(context, ReactionListenerService::class.java).apply {
                putExtra("username", username)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) friendsVM.loadMore()
    }


    Column(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            state = pagerState, modifier = Modifier.weight(1f)
        ) { pageIndex ->
            when (pageIndex) {
                0 -> Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(vertical = 7.dp),
                                    text = "LastAir",
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


                        var showAccountSheet by remember { mutableStateOf(false) }
                        val currentUser = authViewModel.currentUser
                        Surface(
                            modifier = Modifier
                                .wrapContentSize()
                                .size(48.dp)
                                .clickable(onClick = {
                                    showAccountSheet = true
                                }),
                            shape = RoundedCornerShape(50.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoggedIn && accountImageUrl != null) {
                                    AsyncImage(
                                        model = accountImageUrl,
                                        contentDescription = "Profile",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else {
                                    // Placeholder si déconnecté
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        modifier = Modifier.size(30.dp),
                                        contentDescription = "Settings",

                                        )
                                }


                            }
                        }

                        if (showAccountSheet) {
                            ModalBottomSheet(
                                sheetState = sheetState,
                                onDismissRequest = {
                                    showAccountSheet = false
                                }
                            ) {
                                Account(
                                    onNavigateToSettings = {
                                        scope.launch {
                                            sheetState.hide()
                                            showAccountSheet = false
                                            navController.navigate(
                                                Routes.SETTINGS
                                            )
                                        }
                                    },
                                    navController = navController,
                                    authViewModel = authViewModel,
                                    accountName = userProfile?.lastfmUsername ?: "",
                                    accountImageUrl = FirebaseAuth.getInstance().currentUser?.photoUrl?.toString(),
                                    isLoggedIn = userProfile != null,
                                    onClose = { showAccountSheet = false },
                                    onLoginClick = { /* plus tard */ }
                                )

                            }
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
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding(),
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = "Recent listenings",
                                            modifier = Modifier.padding(
                                                horizontal = 20.dp, vertical = 10.dp
                                            ),
                                            style = TextStyle(
                                                fontFamily = robotoFlexVariable,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = (1).sp
                                            )
                                        )
                                    }
                                    TextButton(onClick = {
                                        navController.navigate(
                                            Routes.showAll(
                                                "tracks"
                                            )
                                        )
                                    }) {
                                        Text("Show all")
                                    }

                                }
                                if (isLoading) {
                                    Loading(modifier = Modifier.fillMaxSize())
                                } else if ((tracks.isNotEmpty())) {

                                    HorizontalCenteredHeroCarousel(
                                        state = rememberCarouselState { tracks.size },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                            .padding(horizontal = 5.dp),
                                        itemSpacing = 8.dp,
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) { index ->

                                        val item = tracks[index]

                                        MusicCard(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .height(360.dp)
                                                .maskClip(MaterialTheme.shapes.extraLarge),
                                            trackName = item.name,
                                            artistName = item.artist,
                                            coverUrl = item.coverUrl
                                        )
                                    }
                                } else {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .padding(vertical = 8.dp),
                                            shape = RoundedCornerShape(17.dp),
                                            color = MaterialTheme.colorScheme.tertiaryContainer,
                                        ) {
                                            Text(
                                                text = "Nothing to show here yet, go listen to music !",
                                                modifier = Modifier.padding(
                                                    vertical = 4.dp,
                                                    horizontal = 8.dp
                                                )
                                            )
                                        }
                                    }
                                }

                            }


                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier,
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = "Top Albums", modifier = Modifier.padding(
                                                horizontal = 16.dp, vertical = 5.dp
                                            ), style = TextStyle(
                                                fontFamily = robotoFlexVariable,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = (1).sp
                                            )
                                        )
                                    }
                                    TextButton(onClick = {
                                        navController.navigate(
                                            Routes.showAll(
                                                "albums"
                                            )
                                        )
                                    }) {
                                        Text("Show all")
                                    }

                                }
                                if (isLoading) {
                                    Loading(modifier = Modifier.fillMaxSize())
                                } else if (topAlbums.isNotEmpty()) {
                                    HorizontalUncontainedCarousel(
                                        modifier = Modifier
                                            .height(205.dp)
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp),
                                        state = rememberCarouselState { topAlbums.size },
                                        itemWidth = 186.dp,
                                        itemSpacing = 8.dp,
                                    ) { index ->
                                        MusicCard(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .height(205.dp)
                                                .maskClip(MaterialTheme.shapes.extraLarge),
                                            trackName = topAlbums[index].name,
                                            artistName = topAlbums[index].artist,
                                            coverUrl = topAlbums[index].coverUrl
                                        )
                                    }

                                } else {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .padding(vertical = 8.dp),
                                            shape = RoundedCornerShape(17.dp),
                                            color = MaterialTheme.colorScheme.tertiaryContainer,
                                        ) {
                                            Text(
                                                text = "Nothing to show here yet, go listen to music !",
                                                modifier = Modifier.padding(
                                                    vertical = 4.dp,
                                                    horizontal = 8.dp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier,
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = "Top Artists", modifier = Modifier.padding(
                                                horizontal = 16.dp, vertical = 5.dp
                                            ), style = TextStyle(
                                                fontFamily = robotoFlexVariable,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = (1).sp
                                            )
                                        )
                                    }
                                    TextButton(onClick = {
                                        navController.navigate(
                                            Routes.showAll(
                                                "artists"
                                            )
                                        )
                                    }) {
                                        Text("Show all")
                                    }

                                }
                                if (isLoading) {
                                    Loading(modifier = Modifier.fillMaxSize())
                                } else if (topArtists.isNotEmpty()) {
                                    HorizontalUncontainedCarousel(
                                        modifier = Modifier
                                            .height(205.dp)
                                            .fillMaxWidth()
                                            .padding(horizontal = 5.dp),
                                        state = rememberCarouselState { topArtists.size },
                                        itemWidth = 186.dp,
                                        itemSpacing = 8.dp,
                                    ) { index ->
                                        val item = topArtists[index]
                                        MusicCard(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .height(205.dp)
                                                .maskClip(MaterialTheme.shapes.extraLarge),
                                            trackName = topArtists[index].name,
                                            artistName = topArtists[index].artist,
                                            coverUrl = topArtists[index].coverUrl
                                        )
                                    }
                                } else {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .padding(vertical = 8.dp),
                                            shape = RoundedCornerShape(17.dp),
                                            color = MaterialTheme.colorScheme.tertiaryContainer,
                                        ) {
                                            Text(
                                                text = "Nothing to show here yet, go listen to music !",
                                                modifier = Modifier.padding(
                                                    vertical = 4.dp,
                                                    horizontal = 8.dp
                                                )
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

                1 -> Column(modifier = Modifier.fillMaxSize()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(
                            bottomStart = 20.dp, bottomEnd = 20.dp
                        ), color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Recent Friends Activity", style = TextStyle(
                                    fontFamily = robotoFlexVariable,
                                    fontSize = 27.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = (1).sp,

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
                    ) {

                        when {
                            isFriendsLoading && friendsTracks.isEmpty() -> {
                                Loading(modifier = Modifier.fillMaxSize())
                            }

                            friendsTracks.isEmpty() -> {


                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .padding(vertical = 8.dp, horizontal = 8.dp),
                                        shape = RoundedCornerShape(17.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                    ) {
                                        Text(
                                            text = "Nothing to show here yet, tell your friend to listen to music !",
                                            modifier = Modifier.padding(
                                                vertical = 4.dp,
                                                horizontal = 8.dp
                                            )
                                        )
                                    }
                                }


                            }

                            else -> {
                                LazyColumn(
                                    state = friendsListState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .imePadding()
                                ) {
                                    items(
                                        friendsTracks,
                                        key = { "${it.username}_${it.timestamp}" }) { item ->
                                        val docId = reactionDocId(
                                            from = userProfile?.lastfmUsername ?: "",
                                            to = item.username,
                                            trackName = item.name,
                                            artistName = item.artist,
                                            friendTimestamp = item.timestamp
                                        )
                                        TrackCardShowAll(
                                            trackName = item.name,
                                            artistName = item.artist,
                                            username = item.username,
                                            coverUrl = item.coverUrl,
                                            timestamp = item.timestamp,
                                            isNowPlaying = item.isNowPlaying,
                                            fromUsername = userProfile?.lastfmUsername
                                                ?: "",
                                            isPickerOpen = openPickerKey == "${item.username}_${item.timestamp}",
                                            onPickerOpen = {
                                                openPickerKey =
                                                    "${item.username}_${item.timestamp}"
                                            },
                                            onPickerClose = { openPickerKey = null },
                                            myReaction = myReactions[docId],
                                            onSendReaction = { emoji ->
                                                friendsVM.sendReaction(
                                                    from = userProfile?.lastfmUsername ?: "",
                                                    to = item.username,
                                                    emoji = emoji,
                                                    trackName = item.name,
                                                    artistName = item.artist,
                                                    trackImage = item.coverUrl,
                                                    friendTimestamp = item.timestamp
                                                )
                                            },
                                            onRemoveReaction = { friendsVM.removeReaction(docId) }

                                        )
                                    }
                                    if (isLoadingMore) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center

                                            ) {
                                                Loading()
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }
        }

        CompositionLocalProvider(LocalRippleConfiguration provides null) {

            SecondaryTabRow(pagerState.currentPage, divider = {}, indicator = {
                Box(
                    Modifier
                        .tabIndicatorOffset(
                            pagerState.currentPage, matchContentSize = false
                        )
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(30))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .zIndex(-1f)
                )
            }) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }

                    },
                    interactionSource = remember { MutableInteractionSource() },


                    text = {
                        Text(
                            "Your Feed",
                            color = if (pagerState.currentPage == 0) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },

                    )
                Tab(
                    selected = pagerState.currentPage == 1,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = {
                        Text(
                            "Friends Feed",
                            color = if (pagerState.currentPage == 1) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },


                    )
            }
        }

    }
}
