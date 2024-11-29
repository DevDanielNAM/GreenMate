package com.android.greenmate.presentation.ui.components


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.greenmate.R
import com.android.greenmate.domain.model.Module
import com.android.greenmate.presentation.ui.screens.ChangeMyPlant
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShadow
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.markerCornered
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.Insets
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.copyColor
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun ModulePreviewChart(
    moduleValues: List<Module>,
    moduleValue: String,
    plantId: Long
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    var showChartDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }

    val tabs = listOf("전체", "최근 1개월", "최근 3개월", "최근 6개월", "1년")
    val pagerState = rememberPagerState {
        tabs.size
    }

    LaunchedEffect(moduleValues, moduleValue) {
        when (moduleValue) {
            "soilMoisture" -> dialogTitle = "토양수분"
            "humidity" -> dialogTitle = "습도"
            "temperature" -> dialogTitle = "온도"
            "light" -> dialogTitle = "빛"
        }

        modelProducer.runTransaction {
            lineSeries { series(0) }
        }
        delay(500)
        if(moduleValues.isNotEmpty()) {
            val recentModuleValues = moduleValues.filter { isRecentTimestamp(it.timestamp.time, 12) }

                if (recentModuleValues.isNotEmpty()) {
                    when (moduleValue) {
                        "soilMoisture" -> {
                            modelProducer.runTransaction { lineSeries { series(recentModuleValues.map { it.soilMoisture }) } }
                            dialogTitle = "토양수분"
                        }
                        "humidity" -> {
                            modelProducer.runTransaction { lineSeries { series(recentModuleValues.map { it.humidity }) } }
                            dialogTitle = "습도"
                        }
                        "temperature" -> {
                            modelProducer.runTransaction { lineSeries { series(recentModuleValues.map { it.temperature }) } }
                            dialogTitle = "온도"
                        }
                        "light" -> {
                            modelProducer.runTransaction { lineSeries { series(recentModuleValues.map { it.lightIntensity }) } }
                            dialogTitle = "빛"
                        }
                    }
                }

        }
    }
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    rememberLine(LineCartesianLayer.LineFill.single(fill(Color(0x44FFFFFF))))
                ),
            ),
        ),
        modelProducer,
        modifier = Modifier
            .clickable { showChartDialog = true }
            .background(Color.Transparent, RoundedCornerShape(16.dp))
            .padding(vertical = 10.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
    )
    if(showChartDialog) {
        AlertDialog(
            onDismissRequest = { showChartDialog = false },
            containerColor = Color(0xE9FFFFFF),
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = dialogTitle,
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp)
                    )
                    CustomTabPager(
                        pagerState = pagerState,
                        tabs = tabs,
                        moduleValues, moduleValue, plantId
                    )
//                    ModuleChart(moduleValues, moduleValue, plantId)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showChartDialog = false
                    },
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ){
                    Text(
                        text = "확인",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                        color = Color.Black
                    )
                }
            }
        )
    }
}

fun isRecentTimestamp(timestamp: Long, recentHours: Int): Boolean {
    // 현재 시간
    val currentTime = System.currentTimeMillis()

    val compareTimeInMillis = recentHours * 60 * 60 * 1000L

    return (currentTime - timestamp) <= compareTimeInMillis
}

@Composable
fun CustomTabPager(
    pagerState: PagerState,
    tabs: List<String>,
    moduleValues: List<Module>,
    moduleValue: String,
    plantId: Long
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val coroutineScope = rememberCoroutineScope()
    val recent1MonthModuleValues by remember(moduleValues) {
        mutableStateOf(moduleValues.filter { isRecentTimestamp(it.timestamp.time, 24 * 30) })
    }
    val recent3MonthModuleValues by remember(moduleValues) {
        mutableStateOf(moduleValues.filter { isRecentTimestamp(it.timestamp.time, 24 * 30 * 3) })
    }
    val recent6MonthModuleValues by remember(moduleValues) {
        mutableStateOf(moduleValues.filter { isRecentTimestamp(it.timestamp.time, 24 * 30 * 6) })
    }
    val recent1YearModuleValues by remember(moduleValues) {
        mutableStateOf(moduleValues.filter { isRecentTimestamp(it.timestamp.time, 24 * 365) })
    }

    Column() {
        // 탭 구현
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.padding(bottom = 5.dp),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Color(0xE626A69A), // 인디케이터 색상 변경
                )
            },
            divider = {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(), // 화면 전체 너비
                    thickness = 2.dp, // 테두리 두께
                    color = Color.LightGray // 테두리 색상
                )
            }, // 빈 컴포저블을 지정하여 경계선 제거
            containerColor = Color.Transparent, // 배경색 설정
            contentColor = Color.Black,
            edgePadding = 0.dp, // 패딩 설정
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            title,
                            fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                        )
                   },
                    selected = pagerState.currentPage == index,
                    modifier = Modifier
                        .width(screenWidth / 6f)  // 탭의 가로 크기
                        .height(50.dp), // 탭의 세로 크기
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
        HorizontalPager(state = pagerState) { page ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    when (page) {
                        0 -> ModuleChart(moduleValues, moduleValue, plantId)
                        1 -> ModuleChart(recent1MonthModuleValues, moduleValue, plantId)
                        2 -> ModuleChart(recent3MonthModuleValues, moduleValue, plantId)
                        3 -> ModuleChart(recent6MonthModuleValues, moduleValue, plantId)
                        4 -> ModuleChart(recent1YearModuleValues, moduleValue, plantId)
                    }
                }
            }
        }
    }
}