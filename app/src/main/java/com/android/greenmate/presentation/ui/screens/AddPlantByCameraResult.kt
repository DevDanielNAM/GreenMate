package com.android.greenmate.presentation.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.android.greenmate.R
import com.android.greenmate.presentation.navigation.MainPlantNavItem
import com.android.greenmate.presentation.ui.components.CommonButton
import com.android.greenmate.presentation.ui.components.LottieComponent
import com.android.greenmate.presentation.ui.components.deleteImage
import com.android.greenmate.presentation.viewmodel.MainViewModel
import com.android.greenmate.presentation.viewmodel.MyPlantInputViewModel
import com.android.greenmate.presentation.viewmodel.PlantViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantByCameraResult(
    mainPlantNavController: NavHostController,
    uriString: String,
    viewModel: MainViewModel = hiltViewModel(),
    plantViewModel: PlantViewModel = hiltViewModel(),
    myPlantInputViewModel: MyPlantInputViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val results by viewModel.inferenceResults
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var plantTitle by remember { mutableStateOf("") }
    var plantId by remember { mutableLongStateOf(0L) }
    var plantCategory by remember { mutableStateOf("") }
    var plantImage by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var isRetakingPhoto by remember { mutableStateOf(false) }
    var isSearchingByName by remember { mutableStateOf(false) }

    LaunchedEffect(uriString) {
        val uri = Uri.parse(Uri.decode(uriString))
        val contentResolver = context.contentResolver

        val source = ImageDecoder.createSource(contentResolver, uri)
        bitmap = ImageDecoder.decodeBitmap(source)

        bitmap?.let {
            viewModel.runInference(it)
        }
    }

    // Background Image
    Box(
        modifier = Modifier.fillMaxHeight()
    ) {
        Image(
            painter = painterResource(id = R.drawable.find_camera),
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
                    IconButton(onClick = { mainPlantNavController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Stack"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            val pagerState = rememberPagerState(pageCount = {
                4
            })

            val flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                pagerSnapDistance = PagerSnapDistance.atMost(1),
                decayAnimationSpec = exponentialDecay(
                    frictionMultiplier = 1.5f,  // 마찰력을 약간 줄여 부드러운 스크롤
                    absVelocityThreshold = 0.1f // 속도 임계값 증가
                ),
                snapAnimationSpec = tween(
                    easing = LinearOutSlowInEasing,
                    durationMillis = 500
                ),
                snapPositionalThreshold = 0.2f
            )

            if (results.isNotEmpty()) {
                plantTitle = results[0].label
                plantViewModel.getPlantByTitle(plantTitle)

                HorizontalPager(
                    state = pagerState,
                    flingBehavior = flingBehavior,
                    pageSpacing = 5.dp
                ) { page ->
                    if(page != 3) {
                        val result = results[page]
                        plantTitle = result.label

                        // 각 페이지에 대해 description을 요청
                        LaunchedEffect(plantTitle) {
                            plantViewModel.getPlantByTitle(plantTitle)
                        }

                        val currentPlantId = plantViewModel.getPlantIdByTitle(plantTitle)
                        if (currentPlantId != null) {
                            plantId = currentPlantId
                        }

                        val currentPlantCategory = plantViewModel.getCategoryByTitle(plantTitle)
                        if (currentPlantCategory != null) {
                            plantCategory = currentPlantCategory
                        }

                        // 현재 페이지에 대한 description 가져오기
                        val currentDescription = plantViewModel.getDescriptionByTitle(plantTitle)
                        // 현재 페이지에 대한 plantImage 가져오기
                        val currentPlantImage = plantViewModel.getPlantImageByTitle(plantTitle)

                        if (currentPlantImage != null) {
                            plantImage = currentPlantImage
                        }

                        val resourceId = context.resources.getIdentifier(
                            currentPlantImage ?: "no_image",
                            "drawable", context.packageName)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.85f)
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 10.dp)
                                .background(
                                    color = Color(0xA8FFFFFF),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    1.5.dp,
                                    Color(0xFF105D5E),
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            items(1) {
                                Column(
                                    modifier = Modifier
                                        .padding(bottom = 5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(15.dp)
                                            .clip(shape = RoundedCornerShape(size = 8.dp))
                                    ) {
                                        Image(
                                            painter = painterResource(id = resourceId),
                                            contentDescription = ""
                                        )
                                    }
                                    Text(
                                        modifier = Modifier.padding(horizontal = 17.dp),
                                        text = "${result.label}: ${result.probability}%",
                                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                        fontSize = 20.sp
                                    )
                                    currentDescription?.let {
                                        Text(
                                            modifier = Modifier.padding(20.dp, 10.dp, 20.dp, 10.dp),
                                            text = it,
                                            fontFamily = FontFamily(Font(R.font.spoqahansansneo_regular)),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.85f)
                                .padding(16.dp)
                                .background(
                                    color = Color(0x64000000),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(200.dp)
                            ) {
                                LottieComponent(fileName = "wrong", modifier = Modifier)
                            }
                            Column(
                                modifier = Modifier.fillMaxHeight(0.5f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "주인님이 없나요?",
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    color = Color.White
                                )
                                Text(
                                    text = "가이드에 맞게 다시 촬영하거나",
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    color = Color.White
                                )
                                Text(
                                    text = "주인님 이름으로 검색해보세요",
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                Row(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) Color(0xFF105D5E) else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if(pagerState.currentPage != 3) {
                    CommonButton(
                        name = "주인님 이름이 맞아요",
                        Modifier.fillMaxWidth())
                    {
                        myPlantInputViewModel.plantId = plantId
                        myPlantInputViewModel.category = plantCategory
                        myPlantInputViewModel.alias = plantTitle
                        myPlantInputViewModel.image = plantImage
                        myPlantInputViewModel.date = Date()

                        myPlantInputViewModel.insertMyPlant()

                        mainPlantNavController.navigate(MainPlantNavItem.MyPlantMain.route) {
                            popUpTo(MainPlantNavItem.MyPlantMain.route) { inclusive = true }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CommonButton(name = "다시 촬영", modifier = Modifier.width(screenWidth * 0.45f)) {
                            showDialog = true
                            isRetakingPhoto = true
                        }
                        CommonButton(name = "이름 검색", Modifier.width(screenWidth * 0.45f)) {
                            showDialog = true
                            isSearchingByName = true
                        }
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = true }, // 팝업 외부 클릭 시 닫기
                            containerColor = Color(0xE9FFFFFF),
                            modifier = Modifier.height(screenWidth * 0.4f),
                            text = {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(text = "방금 촬영한 주인님 사진을 삭제하시겠어요?")
                                }
                            },
                            dismissButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                                    onClick = {
                                        if(isRetakingPhoto) {
                                            showDialog = false
                                            mainPlantNavController.popBackStack()
                                        } else if(isSearchingByName) {
                                            showDialog = false
                                            mainPlantNavController.navigate(MainPlantNavItem.AddPlantName.route)
                                        }
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(text = "취소", color = Color.Black, fontFamily = FontFamily(Font(R.font.dohyeon_regular)),)
                                }
                            },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                                    onClick = {
                                        if(isRetakingPhoto) {
                                            showDialog = false
                                            mainPlantNavController.popBackStack()
                                            deleteImage(context, uriString)
                                        }else if(isSearchingByName) {
                                            showDialog = false
                                            mainPlantNavController.navigate(MainPlantNavItem.AddPlantName.route) {
                                                popUpTo(MainPlantNavItem.AddPlantCamera.route) { inclusive = true }
                                            }
                                            deleteImage(context, uriString)
                                        }
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(text = "확인", color = Color.Black, fontFamily = FontFamily(Font(R.font.dohyeon_regular)),)
                                }
                            }
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .background(
                                color = Color(0xA8FFFFFF),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .wrapContentHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                        ) {
                            LottieComponent("analyze_plant", Modifier)
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            textAlign = TextAlign.Center,
                            text = "촬영한 주인님을 분석 중이에요",
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                        )
                    }
                }
            }
        }
    }
}