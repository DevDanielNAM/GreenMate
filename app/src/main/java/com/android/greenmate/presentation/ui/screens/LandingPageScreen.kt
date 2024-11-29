package com.android.greenmate.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.greenmate.R
import com.android.greenmate.presentation.navigation.MainPlantNavItem
import com.android.greenmate.presentation.ui.components.CommonButton

@Composable
fun LandingPageScreen(
    mainPlantNavController: NavHostController,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Background Image
    Box(
        modifier = Modifier.height(screenHeight)
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_0),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .height(screenHeight / 2.5f)
                .width(screenWidth)
                .padding(horizontal = 30.dp)
                .background(Color(0x75000000), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Green Mate",
                    textAlign = TextAlign.Center,
                    fontSize = 40.sp,
                    fontFamily = FontFamily(Font(R.font.rowdies_regular)),
                    color = Color(0xFFFCF8F7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
                Text(
                    text = "식집사가 된 걸 환영해요",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.spoqahansansneo_medium)),
                    color = Color(0xFFFCF8F7),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "이제부터",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.spoqahansansneo_medium)),
                    color = Color(0xFFFCF8F7),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "새로운 주인님을 모셔봐요",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.spoqahansansneo_medium)),
                    color = Color(0xFFFCF8F7),
                    modifier = Modifier.fillMaxWidth()
                )
                CommonButton(name = "주인님 모시기", modifier = Modifier) {
                    mainPlantNavController.navigate(MainPlantNavItem.AddPlant.route)
                }
            }
        }
    }
}