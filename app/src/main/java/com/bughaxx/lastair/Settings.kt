package com.bughaxx.lastair

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SensorDoor
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bughaxx.lastair.ui.theme.robotoFlexVariable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalTextApi::class)
@Composable
fun Settings(
    navController: NavController, authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {


            Surface(
                modifier = Modifier
                    .wrapContentSize(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Settings",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = TextStyle(
                            fontFamily = robotoFlexVariable,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (1).sp
                        )
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .wrapContentSize(),
                shape = RoundedCornerShape(15.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Developed by bughaxx",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = TextStyle(
                            fontFamily = robotoFlexVariable,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (1).sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 6.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {


                    /*main settings screen*/

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ) {

                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {

                            Spacer(modifier = Modifier.height(5.dp))


                            /*write settings here*/
                            Surface(
                                /*Setting1*/
                                modifier = Modifier
                                    .padding(3.dp)
                                    .clickable(onClick = {
                                        val intent =
                                            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                        intent.data = Uri.parse("package:${context.packageName}")
                                        context.startActivity(intent)
                                    }),
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(15),
                            ) {
                                Box(
                                    modifier = Modifier.padding(
                                        vertical = 15.dp,
                                        horizontal = 8.dp
                                    )
                                ) {
                                    Column()
                                    {
                                        Text(
                                            "Allow background activity",
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.padding(bottom = 8.dp),
                                        )


                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            shape = RoundedCornerShape(15),
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                                        ) {
                                            Text(
                                                "Better notifications",
                                                style =
                                                    TextStyle(
                                                        fontStyle = FontStyle.Italic,
                                                    ),
                                                modifier = Modifier.padding(8.dp),
                                                color = MaterialTheme.colorScheme.onSecondaryContainer


                                            )
                                        }
                                    }
                                }
                            }
                        }





                        Spacer(modifier = Modifier.height(5.dp))
                    }

                }





                Surface(
                    /*sign out button*/
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .clickable { authViewModel.signOut() },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(25),

                    ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Sign Out",
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            modifier = Modifier.padding(10.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.SensorDoor,
                            contentDescription = "Sign Out",
                            tint = MaterialTheme.colorScheme.inverseOnSurface,
                        )

                    }


                }
            }
        }
    }


}