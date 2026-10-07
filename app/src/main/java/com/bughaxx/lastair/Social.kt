package com.bughaxx.lastair

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bughaxx.lastair.ui.theme.robotoFlexVariable

@OptIn(ExperimentalTextApi::class)
@Composable
fun Social(
    navController: NavController
) {

    Column(modifier = Modifier.fillMaxSize()) {


        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 6.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .wrapContentSize()
                        .padding(bottom = 15.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Social coming soon",
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
                        .wrapContentSize()
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LastAir",
                            modifier = Modifier.padding(horizontal = 7.dp),
                            style = TextStyle(
                                fontFamily = com.bughaxx.lastair.ui.theme.robotoFlexVariable,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (1).sp,
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                }
            }


        }
    }
}