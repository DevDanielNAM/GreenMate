package com.android.greenmate.presentation.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.android.greenmate.R
import com.android.greenmate.presentation.navigation.MainPlantNavItem
import com.android.greenmate.presentation.ui.components.ImageFromUri
import com.android.greenmate.presentation.viewmodel.MyPlantViewModel
import com.android.greenmate.presentation.viewmodel.PlantViewModel
import com.android.greenmate.presentation.viewmodel.RecordDeleteViewModel
import com.android.greenmate.presentation.viewmodel.RecordInputViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlantDiseaseScreen(
    mainPlantNavController: NavHostController,
    plantViewModel: PlantViewModel = hiltViewModel(),
    myPlantViewModel: MyPlantViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val myPlantDesease by plantViewModel.plantDisease.collectAsStateWithLifecycle()
//    val favoriteMyPlant by myPlantViewModel.favoriteMyPlant.collectAsStateWithLifecycle()
//    val myPlantId = favoriteMyPlant?.myPlantId ?: 0L
    val myPlantId by myPlantViewModel.favoriteMyPlantId.observeAsState()

    myPlantId.takeIf { it != 0L }?.let { plantViewModel.getDiseasesByPlantId(it) }

    // Background Image
    Box(
        modifier = Modifier.fillMaxHeight()
    ) {
        Image(
            painter = painterResource(id = R.drawable.find_name),
            contentDescription = "",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxHeight()
        )
    }

    Scaffold(
        containerColor = Color(0x98FFFFFF),
        topBar = {
            TopAppBar(
                title = {
                },
                colors = TopAppBarColors(Color.Transparent, Color.Transparent, Color.Black, Color.Transparent, Color.Transparent),
                navigationIcon = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { mainPlantNavController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back Stack"
                            )
                        }
                        IconButton(
                            onClick = {
                                mainPlantNavController.navigate(MainPlantNavItem.MyPlantMain.route) {
                                    popUpTo(MainPlantNavItem.MyPlantMain.route) { inclusive = true }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Out",
                                tint = Color.Red
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight)
                .padding(innerPadding)
        ) {
            item {
                val groupedDiseases = myPlantDesease.takeIf { it.isNotEmpty() }?.groupBy { it.title }

                Text(
                    text = "혹시 주인님이 아픈가요?",
                    fontSize = 35.sp,
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )

                groupedDiseases?.forEach { (title, diseases) ->
                    // 각 disease의 descriptions 리스트를 하나의 문자열로 합치기
                    val combinedDescription = diseases
                        .flatMap { it.descriptions }  // 모든 descriptions를 하나의 리스트로
                        .joinToString("\n\n")  // 개행으로 구분

                    DiseaseExpandableCard(title, combinedDescription, myPlantId, mainPlantNavController)
                }
            }
        }
    }

}

@Composable
fun DiseaseExpandableCard(
    title: String,
    description: String,
    myPlantId: Long?,
    mainPlantNavController: NavHostController,
    recordInputViewModel: RecordInputViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var expandedState by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0x75000000)
        ),
        onClick = {
            expandedState = !expandedState
        },
        modifier = Modifier
            .width(screenWidth / 1.1f)
            .wrapContentHeight()
            .padding(5.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title.removeSuffix("."),
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(start = 10.dp)
                )
                IconButton(
                    modifier = Modifier
                        .alpha(0.5f)
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            if (expandedState) {
                Column {
                    Text(
                        text = description,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontFamily = FontFamily(Font(R.font.spoqahansansneo_medium)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .padding(horizontal = 10.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp)
                            .padding(end = 10.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .background(Color(0xA136FA95), RoundedCornerShape(16.dp))
                                .wrapContentHeight()
                                .width(screenWidth / 3f)
                                .alpha(0.85f),
                            onClick = {
                                recordInputViewModel.myPlantId.value = myPlantId
                                recordInputViewModel.title.value = title.removeSuffix(".")
                                recordInputViewModel.content.value = description
                                recordInputViewModel.date.value = Date()

                                recordInputViewModel.insertRecord()

                                mainPlantNavController.navigate(MainPlantNavItem.MyPlantMain.route) {
                                    popUpTo(MainPlantNavItem.MyPlantMain.route) { inclusive = true }
                                }
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddTask,
                                    contentDescription = "Add",
                                    tint = Color.White
                                )
                                Text(
                                    text = "기록하기",
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    fontSize = 17.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}