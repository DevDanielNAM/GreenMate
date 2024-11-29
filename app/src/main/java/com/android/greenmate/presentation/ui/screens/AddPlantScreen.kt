package com.android.greenmate.presentation.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.greenmate.R
import com.android.greenmate.presentation.navigation.CameraNavItem
import com.android.greenmate.presentation.navigation.MainPlantNavItem
import com.android.greenmate.presentation.ui.components.CommonButton

@Composable
fun AddPlantScreen(
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
                        .padding(bottom = 10.dp, top = 5.dp)
                )

                Text(
                    text = "나의 주인님은..?",
                    textAlign = TextAlign.Center,
                    fontSize = 33.sp,
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    color = Color(0xFFFCF8F7),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ChooseButton(name = "사진으로", painterResource(id = R.drawable.find_camera), modifier = Modifier) {
                        mainPlantNavController.navigate("camera/${Uri.encode("add")}")
                    }

                    ChooseButton(name = "이름으로", painterResource(id = R.drawable.find_name), modifier = Modifier) {
                        mainPlantNavController.navigate(MainPlantNavItem.AddPlantName.route)
                    }
                }
            }
        }
    }
}

@Composable
fun ChooseButton(
    name: String,
    painter: Painter,
    modifier: Modifier,
    fontSize: TextUnit = 18.sp,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box (
        modifier = modifier
            .width(screenWidth / 2.5f)
            .padding(10.dp, 16.dp),
    ){
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .height(screenHeight / 5),
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight / 5),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp,Color(0xFFFCF8F7)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x31201F1F))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = name,
                    fontSize = fontSize,
                    fontFamily = FontFamily(Font(R.font.spoqahansansneo_medium)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 25.sp
                )
            }
        }
    }
}
