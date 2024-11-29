package com.android.greenmate.presentation.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.greenmate.R
import com.android.greenmate.data.ble.BleManager
import com.android.greenmate.presentation.viewmodel.MyPlantViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MyPlantMainScreen(
    mainPlantNavController: NavHostController,
    bleManager: BleManager,
    pagerState: PagerState,
    myPlantViewModel: MyPlantViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val coroutineScope = rememberCoroutineScope()

    val existMyPlants by myPlantViewModel.existMyPlants.observeAsState()
    val myPlants by myPlantViewModel.myPlants.observeAsState()

    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1),
        decayAnimationSpec = exponentialDecay(
            frictionMultiplier = 1.5f,  // 마찰력을 약간 줄여 부드러운 스크롤
            absVelocityThreshold = 0.1f // 속도 임계값 증가
        ),
        snapAnimationSpec =
        tween(
            easing = LinearOutSlowInEasing,
            durationMillis = 600
        ),
        snapPositionalThreshold = 0.25f
    )

    when (existMyPlants) {
        true -> {
            VerticalPager(
                state = pagerState,
                flingBehavior = flingBehavior,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight),
                userScrollEnabled = true,
                pageSpacing = 0.dp
            ) { page ->
                when (page) {
                    0 -> {
                        Box(
                            modifier = Modifier.height(screenHeight)
                        ) {
                            MyPlantScreen(mainPlantNavController, bleManager)
                        }
                    }

                    1 -> {
                        Box(
                            modifier = Modifier.height(screenHeight)
                        ) {
                            AlarmDiaryWateringScreen(pagerState)
                        }
                    }

                    2 -> {
                        Box(
                            modifier = Modifier.height(screenHeight)
                        ) {
                            myPlants?.let { MyPlantsDescriptionScreen(it, pagerState) }
                        }
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    if (pagerState.currentPage == iteration) {
                        Box(
                            modifier = Modifier
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color)
                                .width(20.dp)
                                .height(30.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(20.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            iteration, animationSpec = tween(
                                                durationMillis = 500,
                                                delayMillis = 100,
                                                easing = LinearOutSlowInEasing
                                            )
                                        )
                                    }
                                }
                        )
                    }
                }
            }
        }

        false -> {
            LandingPageScreen(mainPlantNavController)
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White), // 스플래시 배경색
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_img1080), // 스플래시 이미지
                    contentDescription = null,
                    modifier = Modifier.width(screenWidth)
                )
            }
        }
    }
}