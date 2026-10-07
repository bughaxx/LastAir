package com.bughaxx.lastair

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bughaxx.lastair.ui.theme.HomeScreen

object Routes {
    const val HOME = "home"
    const val SOCIAL = "social"
    const val FRIENDS = "friends"
    const val INBOX = "inbox"

    const val SETTINGS = "settings"

    const val SHOW_ALL = "show_all/{type}"
    fun showAll(type: String) = "show_all/$type"
}

data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    deepLinkUri: Uri? = null,
    pendingRoute: String?,
    onRouteConsumed: () -> Unit,
) {

    val navController = rememberNavController()
    val isProfileLoaded by authViewModel.isProfileLoaded.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    val bottomBarVisible = remember { mutableStateOf(true) }
    val bottomBarHeight = 80.dp

    val bottomPadding by animateDpAsState(
        targetValue = if (bottomBarVisible.value) bottomBarHeight else 0.dp,
        animationSpec = tween(300),
        label = "bottom_padding"
    )


    val scrollBehavior = remember {
        object : NestedScrollConnection {
            var accumulatedDy = 0f

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                accumulatedDy += available.y

                if (accumulatedDy > 50) { // scroll vers le haut → montrer
                    bottomBarVisible.value = true
                    accumulatedDy = 0f
                } else if (accumulatedDy < -50) { // scroll vers le bas → cacher
                    bottomBarVisible.value = false
                    accumulatedDy = 0f
                }

                return Offset.Zero
            }
        }
    }

    val previousUser = remember { mutableStateOf(currentUser) }

    val items = listOf(
        BottomNavItem("Feed", Icons.Default.MusicNote, Routes.HOME),
        BottomNavItem("Social", Icons.Default.AddReaction, Routes.SOCIAL),
        BottomNavItem("Friends", Icons.Default.People, Routes.FRIENDS),
        BottomNavItem("Inbox", Icons.Default.Inbox, Routes.INBOX),
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(currentUser, userProfile, isProfileLoaded) {
        if (!isProfileLoaded) return@LaunchedEffect
        when {
            currentUser == null -> navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }

            currentUser != null && userProfile?.lastfmUsername == null -> { }

            else -> navController.navigate(Routes.HOME) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    previousUser.value = currentUser

    LaunchedEffect(deepLinkUri) {
        android.util.Log.d("LastAir", "deepLinkUri reçu: $deepLinkUri")
        val token = deepLinkUri?.getQueryParameter("token")
        android.util.Log.d("LastAir", "token extrait: $token")
        if (token != null) {
            authViewModel.handleLastFmCallback(token)
        }
    }

    LaunchedEffect(pendingRoute) {
        pendingRoute?.let { route ->
            navController.navigate(route)
            onRouteConsumed()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior),
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (currentUser == null) "login" else Routes.HOME,
                modifier = Modifier.padding(innerPadding).padding(bottom = bottomPadding),
                enterTransition = {
                    // Si on vient d'un écran de type "Détails" (ShowAll), on fait un fondu simple
                    if (initialState.destination.route?.startsWith("show_all/") == true) {
                        fadeIn(tween(300))
                    } else {
                        // Sinon (navigation entre onglets), fondu + léger zoom
                        fadeIn(tween(300)) + scaleIn(initialScale = 0.92f)
                    }
                },
                exitTransition = {
                    // Si on va vers un écran de Détails, on glisse vers la gauche (effet hiérarchique)
                    if (targetState.destination.route?.startsWith("show_all/") == true) {
                        fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
                    } else {
                        // Entre onglets, fondu simple
                        fadeOut(tween(250))
                    }
                },
                popEnterTransition = {
                    // Quand on revient d'un écran de Détails
                    if (initialState.destination.route?.startsWith("show_all/") == true) {
                        fadeIn(tween(300)) + slideInHorizontally { -it / 2 }
                    } else {
                        fadeIn(tween(300))
                    }
                },
                popExitTransition = {
                    // Sortie classique en fondu pour le retour arrière
                    fadeOut(tween(250)) + scaleOut(targetScale = 0.92f)
                }
            )
            {
                composable("login") { LoginScreen(authViewModel) }
                composable(Routes.HOME) {
                    HomeScreen(
                        authViewModel = authViewModel,
                        navController = navController,

                        )
                }
                composable(
                    Routes.SOCIAL,
                ) {
                    Social(
                        navController = navController
                    )
                }
                composable(
                    Routes.FRIENDS,
                ) {
                    Friends(
                        navController = navController
                    )
                }
                composable(
                    Routes.INBOX,
                ) {
                    Inbox(
                        navController = navController
                    )
                }
                composable("lastfm_connect") { LastFmConnectScreen(authViewModel) }
                composable("show_all/{type}") { backStackEntry ->
                    val type = backStackEntry.arguments?.getString("type")
                    ShowAllScreen(
                        type = type ?: "tracks",
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
                composable(
                    Routes.SETTINGS,

                    ) {
                    Settings(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }

            }


        }
        AnimatedVisibility(
            visible = currentRoute != "login" && bottomBarVisible.value,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            AppBottomBar(navController = navController, items = items)
        }
    }


}


@Composable
fun AppBottomBar(navController: NavController, items: List<BottomNavItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    NavigationBar(
        modifier = Modifier
            .shadow(20.dp)
            .clip(
                RoundedCornerShape(
                    topEnd = 45.dp,
                    topStart = 45.dp,
                )
            ),


        ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}