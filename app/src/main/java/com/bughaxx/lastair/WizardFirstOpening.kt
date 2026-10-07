package com.bughaxx.lastair

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bughaxx.lastair.ui.theme.robotoFlexVariable

@OptIn(ExperimentalTextApi::class)
@Composable
fun LoginScreen(authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val currentUser by authViewModel.currentUser.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = if (currentUser != null) 1 else 0,
        pageCount = { 2 }
    )


    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            pagerState.animateScrollToPage(1)
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            userScrollEnabled = false,
            state = pagerState, modifier = Modifier.weight(1f)
        ) { pageIndex ->
            when (pageIndex) {

                0 ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,

                            ) {
                            Spacer(modifier = Modifier.weight(1f))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp),
                            ) {


                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(10.dp)
                                ) {

                                    Text(
                                        text = "Welcome to",
                                        style = TextStyle(
                                            fontFamily = robotoFlexVariable,
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = (0).sp
                                        )
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,

                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(6.dp),
                                            text = "LastAir",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = TextStyle(
                                                fontFamily = robotoFlexVariable,
                                                fontSize = 48.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = (3).sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(15.dp))


                                    Text(
                                        text = "The only music social network you need",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant

                                    )
                                    Spacer(modifier = Modifier.height(40.dp))

                                    Text(
                                        text = "Sign up or sign in here :",
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )


                                    Button(
                                        onClick = { authViewModel.signInWithGoogle(context) },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        contentPadding = PaddingValues(
                                            horizontal = 24.dp,
                                            vertical = 14.dp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(70.dp)
                                            .shadow(
                                                elevation = 30.dp,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .size(60.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Login,
                                                contentDescription = "Google Sign-In/Sign-Up",
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = "Continue with Google",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }

                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(18.dp),

                                ) {
                                Text(
                                    text = "Made by 💛 by bughaxx",
                                    modifier = Modifier.padding(6.dp)
                                )
                            }


                        }


                    }

                1 -> LastFmConnectScreen(authViewModel = authViewModel)
            }
        }
    }
}

