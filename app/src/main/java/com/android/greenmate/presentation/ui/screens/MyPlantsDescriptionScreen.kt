package com.android.greenmate.presentation.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.greenmate.R
import com.android.greenmate.domain.model.MyPlant
import com.android.greenmate.presentation.ui.components.LottieComponent
import com.android.greenmate.presentation.viewmodel.MyPlantViewModel
import com.android.greenmate.presentation.viewmodel.PlantViewModel
import kotlinx.coroutines.Dispatchers

@Composable
fun MyPlantsDescriptionScreen(
    myPlants: List<MyPlant>,
    mainPagerState: PagerState,
    plantViewModel: PlantViewModel = hiltViewModel(),
    myPlantViewModel: MyPlantViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val context = LocalContext.current

    myPlantViewModel.getAllMyPlants()
    myPlantViewModel.getFavoriteMyPlant()

    val myPlantId by myPlantViewModel.favoriteMyPlantId.observeAsState()
    val myPlantIds by myPlantViewModel.myPlantIds.observeAsState()

    val pagerState = rememberPagerState(pageCount = { myPlants.size })

    val initialPage = remember(myPlantId, myPlantIds) {
        myPlantIds!!.indexOfFirst { it == myPlantId }
    }

    val newMyPlants = remember(myPlants, initialPage) {
        val list = myPlants.toMutableList()
        if (initialPage >= 0) {
            val temp = list[initialPage]
            list.removeAt(initialPage)
            list.add(0, temp)
        }
        list
    }

    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1),
        decayAnimationSpec = exponentialDecay(
            frictionMultiplier = 1.5f,  // 마찰력을 약간 줄여 부드러운 스크롤
            absVelocityThreshold = 0.1f // 속도 임계값 증가
        ),
        snapAnimationSpec = tween(
            easing = LinearOutSlowInEasing,
            durationMillis = 450
        ),
        snapPositionalThreshold = 0.25f
    )

    val plantInfo by plantViewModel.plantInfo.collectAsStateWithLifecycle()

    val backgroundResourceId by remember {
        derivedStateOf {
            val category = when(newMyPlants[pagerState.currentPage].category) {
                "leaf" -> "main_leaf"
                "flower" -> "main_flower"
                "cactus" -> "main_cactus"
                else -> "main_fruit"
            }
            context.resources.getIdentifier(category, "drawable", context.packageName)
        }
    }

    val backgroundPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(backgroundResourceId)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .build(),
        contentScale = ContentScale.FillHeight
    )

    // Background Image
    Box(
        modifier = Modifier.height(screenHeight)
    ) {
        Image(
            painter = backgroundPainter,
            contentDescription = "",
            contentScale = ContentScale.FillHeight
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(2f)
    ) {
        if(pagerState.currentPage == 0 && newMyPlants.size != 1) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(screenWidth / 5f)
                ) {
                    LottieComponent(fileName = "next_button", modifier = Modifier)
                }
            }
        } else if(pagerState.currentPage == newMyPlants.size - 1 && newMyPlants.size != 1) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(screenWidth / 5f)
                ) {
                    LottieComponent(fileName = "prev_button", modifier = Modifier)
                }
            }
        } else if(pagerState.currentPage in 1..newMyPlants.size - 2) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(screenWidth / 5f)
                ) {
                    LottieComponent(fileName = "prev_button", modifier = Modifier)
                }

                Box(
                    modifier = Modifier
                        .size(screenWidth / 5f)
                ) {
                    LottieComponent(fileName = "next_button", modifier = Modifier)
                }
            }
        }
    }


    LaunchedEffect(mainPagerState.currentPage != 2) {
        pagerState.animateScrollToPage(
            page = 0,
            animationSpec = spring(stiffness = 1000f)
        )
    }


    HorizontalPager(
        state = pagerState,
        flingBehavior = flingBehavior,
        modifier = Modifier
            .background(Color(0x75FCF8F7))
            .fillMaxWidth()
            .height(screenHeight),
    ) { page ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
        ) { item()
            {
                Row(
                    modifier = Modifier
                        .width(screenWidth)
                ) {
                    val resourceId = context.resources.getIdentifier(
                        newMyPlants[page].image,
                        "drawable",
                        context.packageName
                    )
                    val plantPainter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(resourceId)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .dispatcher(Dispatchers.IO)
                            .build(),
                        contentScale = ContentScale.Crop
                    )

                    LaunchedEffect(pagerState.currentPage != page) {
                        plantViewModel.getPlantInfoById(newMyPlants[pagerState.currentPage].plantId)
                    }

                    Column(
                        modifier = Modifier
                            .width(screenWidth)
                            .padding(horizontal = 10.dp)
                            .padding(top = 5.dp)
                    ) {
                        Text(
                            text = newMyPlants[page].alias,
                            fontSize = if(newMyPlants[page].alias.length > 14) 25.sp else 35.sp,
                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                            textDecoration = TextDecoration.Underline,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        )
                        plantInfo?.let {item->
                            Text(
                                text = "(${item.korName})",
                                fontSize = if (item.korName.length > 14) 12.sp else 18.sp,
                                fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                lineHeight = 30.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .background(Color(0x88FFFFFF), RoundedCornerShape(10.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(screenWidth / 2f)
                                    .padding(horizontal = 5.dp)
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

                            Column(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .wrapContentHeight()
                            ) {
                                plantInfo?.let { it1 ->
                                    PlantSummary(Color(0xFFFA4D16), it1.light, it1.lightSummary){
                                        Icon(
                                            imageVector = Icons.Outlined.WbSunny,
                                            contentDescription = null,
                                            tint = Color(0xFFFF7043)
                                        )
                                    }

                                    PlantSummary(Color(0xFF296DF6), it1.water, it1.waterSummary){
                                        Icon(
                                            imageVector = Icons.Outlined.WaterDrop,
                                            contentDescription = null,
                                            tint = Color(0xFF296DF6)
                                        )
                                    }

                                    PlantSummary(Color(0xFF42A5F5), it1.humidity, it1.humiditySummary){
                                        Icon(
                                            painter = painterResource(id = R.drawable.humidity_percentage_24px),
                                            contentDescription = null,
                                            tint = Color(0xFF42A5F5)
                                        )
                                    }

                                    PlantSummary(Color(0xFFEF5350), it1.temperature, it1.temperatureSummary){
                                        Icon(
                                            imageVector = Icons.Outlined.Thermostat,
                                            contentDescription = null,
                                            tint = Color(0xFFEF5350)
                                        )
                                    }
                                }
                            }
                        }


                        val infoPagerState = rememberPagerState(pageCount = { 4 })
                        val label = arrayOf("햇빛", "물", "습도", "온도")

                        var showLightInfotDialog by remember { mutableStateOf(false) }
                        var showWaterInfotDialog by remember { mutableStateOf(false) }
                        var showHumidityInfotDialog by remember { mutableStateOf(false) }
                        var showTemperatureInfotDialog by remember { mutableStateOf(false) }

                        Column(
                            Modifier
                                .background(Color.Transparent)
                                .wrapContentHeight()
                                .heightIn(max = screenHeight / 1.7f)
                                .fillMaxWidth(),
                        ) {
                            Row(
                                Modifier
                                    .padding(bottom = 10.dp)
                                    .wrapContentHeight()
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                repeat(infoPagerState.pageCount) { page ->
                                    val size = (screenWidth / 4)
                                    val color = Color(0xFF4DB6AC)

                                    Button(
                                        modifier = Modifier
                                            .width(size - 10.dp)
                                            .height(size / 2.5f),
                                        onClick = {
                                            when (page) {
                                                0 -> showLightInfotDialog = true
                                                1 -> showWaterInfotDialog = true
                                                2 -> showHumidityInfotDialog = true
                                                else -> showTemperatureInfotDialog = true
                                            }
                                        },
                                        shape = when (page) {
                                            0 -> RoundedCornerShape(10.dp)
                                            1 -> RoundedCornerShape(10.dp)
                                            2 -> RoundedCornerShape(10.dp)
                                            else -> RoundedCornerShape(10.dp)
                                        },
                                        colors = ButtonDefaults.buttonColors(color)
                                    ) {
                                        Text(
                                            text = label[page],
                                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                            }
                            if (showLightInfotDialog) {
                                AlertDialog(
                                    onDismissRequest = { showLightInfotDialog = false },
                                    containerColor = Color(0xD9FFFFFF),
                                    text = {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .heightIn(max = screenHeight / 1.4f),
                                            verticalArrangement = Arrangement.SpaceAround
                                        ) {
                                            item {
                                                plantInfo?.let { it ->
                                                    DescriptionTitle(it.light)
                                                    DescriptionText(it.lightDescription + "\n")

                                                    DescriptionTitle("빛이 강하면")
                                                    DescriptionText(it.lightStrong + "\n")

                                                    DescriptionTitle("빛이 약하면")
                                                    DescriptionText(it.lightWeak)
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        DialogConfirmButton(){showLightInfotDialog = false}
                                    }
                                )
                            }
                            if (showWaterInfotDialog) {
                                AlertDialog(
                                    onDismissRequest = { showWaterInfotDialog = false },
                                    containerColor = Color(0xD9FFFFFF),
                                    text = {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(screenHeight / 1.4f),
                                            verticalArrangement = Arrangement.SpaceAround
                                        ) {
                                            item {
                                                plantInfo?.let { it ->
                                                    DescriptionTitle(it.water)
                                                    DescriptionText(it.waterDescription + "\n")

                                                    DescriptionTitle("여름")
                                                    DescriptionText(it.waterSummer + "\n")

                                                    DescriptionTitle("겨울")
                                                    DescriptionText(it.waterWinter)
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        DialogConfirmButton(){showWaterInfotDialog = false}
                                    }
                                )
                            }
                            if (showHumidityInfotDialog) {
                                AlertDialog(
                                    onDismissRequest = { showHumidityInfotDialog = false },
                                    containerColor = Color(0xD9FFFFFF),
                                    text = {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(screenHeight / 1.4f),
                                            verticalArrangement = Arrangement.SpaceAround
                                        ) {
                                            item {
                                                plantInfo?.let { it ->
                                                    DescriptionTitle(it.humidity)
                                                    DescriptionText(it.humidityDescription + "\n")

                                                    DescriptionTitle("습도가 낮으면")
                                                    DescriptionText(it.humidityLow + "\n")

                                                    DescriptionTitle("습도가 높으면")
                                                    DescriptionText(it.humidityHigh)
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        DialogConfirmButton(){showHumidityInfotDialog = false}
                                    }
                                )
                            }
                            if (showTemperatureInfotDialog) {
                                AlertDialog(
                                    onDismissRequest = { showTemperatureInfotDialog = false },
                                    containerColor = Color(0xD9FFFFFF),
                                    text = {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(screenHeight / 1.4f),
                                            verticalArrangement = Arrangement.SpaceAround
                                        ) {
                                            item {
                                                plantInfo?.let { it ->
                                                    DescriptionTitle(it.temperature)
                                                    DescriptionText(it.temperatureDescription + "\n")

                                                    DescriptionTitle("온도가 낮으면")
                                                    DescriptionText(it.temperatureLow + "\n")

                                                    DescriptionTitle("온도가 높으면")
                                                    DescriptionText(it.temperatureHigh + "\n")

                                                    DescriptionTitle("겨울철 관리온도")
                                                    DescriptionText(it.temperatureWinter)
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        DialogConfirmButton(){showTemperatureInfotDialog = false}
                                    }
                                )
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                item {
                                    plantInfo?.let { it1 ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    Color(0xA9FCF8F7),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .padding(5.dp)
                                        ) {
                                            DescriptionText(it1.description)
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
}

@Composable
fun PlantSummary(
    textColor: Color,
    summaryTitle: String,
    summaryContent: String,
    iconContent: @Composable () -> Unit
){
    Row {
        iconContent()
        Column {
            Text(
                text = summaryTitle,
                fontSize = 17.sp,
                fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 3.dp)
            )
            Text(
                text = summaryContent,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(vertical = 3.dp)
            )
        }
    }
}

@Composable
fun DialogConfirmButton(onClick:()->(Unit)) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Text(
            text = "확인",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
            color = Color.Black
        )
    }
}

@Composable
fun DescriptionTitle(
    title: String
) {
    Text(
        text = title,
        textAlign = TextAlign.Center,
        fontSize = 20.sp,
        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
        color = Color.Black
    )
}

@Composable
fun DescriptionText(
    content: String
) {
    Text(
        text = content,
        fontSize = 15.sp,
        fontFamily = FontFamily(Font(R.font.gowun_batang_regular)),
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(5.dp)
    )
}