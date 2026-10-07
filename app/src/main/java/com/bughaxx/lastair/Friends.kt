package com.bughaxx.lastair

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class Friend(
    val name: String,
    val imageUrl: String,
    val playcount: Int
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun Friends(
    authViewModel: AuthViewModel = viewModel(),
    friendsViewModel: FriendsViewModel = viewModel(),
    navController: NavController
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    val friends by friendsViewModel.friends.collectAsState()
    val isLoading by friendsViewModel.isLoading.collectAsState()
    val robotoFlexVariable = FontFamily(
        Font(
            resId = R.font.roboto_flex,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.width(200f),
                    FontVariation.weight(900),
                )
        )
    )

    LaunchedEffect(userProfile?.lastfmUsername) {
        userProfile?.lastfmUsername?.let {
            friendsViewModel.loadFriends(it)
        }
    }



    Column(modifier = Modifier.fillMaxSize()) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Box(
                modifier = Modifier.padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your Friends",
                    style = TextStyle(
                        fontFamily = robotoFlexVariable,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (1).sp
                    )
                )
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 6.dp)
                .animateContentSize(animationSpec = tween(300)),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {

            if (isLoading) {
                Loading(
                    modifier = Modifier.fillMaxSize()
                )


            } else if (friends.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(friends) { friend ->
                        FriendCard(friend = friend)
                    }
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
                            text = "You have no friends yet, follow them on last.fm !",
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }
                }


            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun FriendCard(friend: Friend) {
    val robotoFlexVariable = FontFamily(
        Font(
            resId = R.font.roboto_flex,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.width(200f),
                    FontVariation.weight(900),
                )
        )
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        FriendAvatar(friend)

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.padding(bottom = 5.dp)
        ) {
            Text(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 3.dp
                ),
                text = friend.name,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = TextStyle(
                    fontFamily = robotoFlexVariable,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (1).sp,
                )

            )
        }
        Text(
            text = "${friend.playcount} plays",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }


}

@OptIn(ExperimentalTextApi::class)
@Composable
fun FriendAvatar(friend: Friend) {
    val bgColor = MaterialTheme.colorScheme.primaryContainer
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer
    val robotoFlexVariable = FontFamily(
        Font(
            resId = R.font.roboto_flex,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.width(200f),
                    FontVariation.weight(900),
                )
        )
    )
    if (friend.imageUrl.isBlank()) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = friend.name.firstOrNull()?.uppercase() ?: "?",
                color = textColor,
                style = TextStyle(
                    fontFamily = robotoFlexVariable,
                    fontSize = 60.sp,
                )
            )
        }
    } else {
        AsyncImage(
            model = friend.imageUrl,
            contentDescription = friend.name,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
