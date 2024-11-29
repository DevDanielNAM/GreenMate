package com.android.greenmate.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.BrandingWatermark
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.android.greenmate.R
import com.android.greenmate.presentation.navigation.MainPlantNavItem
import com.android.greenmate.presentation.viewmodel.MyPlantInputViewModel
import kotlinx.coroutines.Dispatchers
import java.util.Date

@Composable
fun SearchItem(
    plantId: Long,
    plantCategory: String,
    plantTitle: String,
    plantImage: String,
    description: String,
    light: String,
    water: String,
    humidity: String,
    temperature: String,
    mainPlantNavController: NavHostController,
    myPlantInputViewModel: MyPlantInputViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var showDialog by remember { mutableStateOf(false) }

    val plantResourceId = context.resources.getIdentifier(
        plantImage,
        "drawable",
        context.packageName
    )

    val plantPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .size(Size.ORIGINAL)
            .data(plantResourceId)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .build(),
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xD9FFFFFF)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(2.dp,Color(0xB2004D40)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(screenWidth)
            .padding(16.dp, 5.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = rememberRipple(color = Color(0xFF004D40)),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (plantImage != "no_image") {
                        showDialog = true
                    }
                }
                .height(screenHeight / 7)
        ) {
            Box(
                modifier = Modifier
                    .width(screenWidth / 4)
                    .border(1.dp, Color.Transparent, RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = plantPainter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            Text(
                text = plantTitle,
                fontSize = 17.sp,
                fontFamily = FontFamily(Font(R.font.spoqahansansneo_medium)),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(end = 10.dp)
            )
        }

        if(showDialog && (plantImage != "no_image")) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = Color(0xFFFFFFFF),
                text = {
                    Column(
                        modifier = Modifier
                            .width(screenWidth)
                    ) {
                        Text(
                            text = plantTitle,
                            fontSize = if(plantTitle.length > 14) 22.sp else 30.sp,
                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                            textDecoration = TextDecoration.Underline,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                        )

                        Row(
                            modifier = Modifier
                                .height(screenHeight / 4.8f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(screenWidth / 3f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                                ) {
                                    Image(
                                        painter = plantPainter,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = AbsoluteAlignment.Left,
                                verticalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 5.dp)
                                    .padding(vertical = 5.dp)
                            ) {
                                PlantSummary(light, 17.sp, Color(0xFFFA4D16)){
                                    Icon(
                                        imageVector = Icons.Outlined.WbSunny,
                                        contentDescription = null,
                                        tint = Color(0xFFFF7043)
                                    )
                                }

                                PlantSummary(water, 17.sp, Color(0xFF296DF6)){
                                    Icon(
                                        imageVector = Icons.Outlined.WaterDrop,
                                        contentDescription = null,
                                        tint = Color(0xFF296DF6)
                                    )
                                }

                                PlantSummary(humidity, 17.sp, Color(0xFF42A5F5)){
                                    Icon(
                                        painter = painterResource(id = R.drawable.humidity_percentage_24px),
                                        contentDescription = null,
                                        tint = Color(0xFF42A5F5)
                                    )
                                }

                                PlantSummary(temperature, 17.sp, Color(0xFFEF5350)){
                                    Icon(
                                        imageVector = Icons.Outlined.Thermostat,
                                        contentDescription = null,
                                        tint = Color(0xFFEF5350)
                                    )
                                }
                            }
                        }

                        Text(
                            text = description,
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.gowun_dodum_regular)),
                            lineHeight = 13.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(top = 10.dp, end = 10.dp)
                        )
                    }
                },
                dismissButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        onClick = {
                            showDialog = false
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "취소",
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                        )
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        onClick = {
                            myPlantInputViewModel.plantId = plantId
                            myPlantInputViewModel.category = plantCategory
                            myPlantInputViewModel.alias = plantTitle
                            myPlantInputViewModel.image = plantImage
                            myPlantInputViewModel.date = Date()

                            myPlantInputViewModel.insertMyPlant()

                            showDialog = false
                            mainPlantNavController.navigate(MainPlantNavItem.MyPlantMain.route) {
                                popUpTo(MainPlantNavItem.MyPlantMain.route) { inclusive = true }
                            }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "추가",
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                            )
                    }
                }
            )
        }
    }
}

@Composable
fun PlantSummary(
    textTitle: String,
    fontSize: TextUnit,
    textColor: Color,
    iconContent: @Composable () -> Unit
){
    Row {
        iconContent()
        Text(
            text = textTitle,
            fontSize = fontSize,
            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
            color = textColor,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(vertical = 3.dp)
        )
    }
}