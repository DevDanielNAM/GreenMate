package com.android.greenmate.presentation.ui.components

import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.greenmate.R
//import com.android.greenmate.presentation.ui.screens.Quadruple
import com.android.greenmate.presentation.viewmodel.CalendarViewModel
import com.android.greenmate.presentation.viewmodel.MyPlantViewModel
import com.android.greenmate.presentation.viewmodel.RecordDeleteViewModel
import com.android.greenmate.presentation.viewmodel.RecordViewModel
import com.android.greenmate.utils.getWeeksOfMonth
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@Composable
fun HorizontalCalendar(
    pagerState: PagerState,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    recordViewModel: RecordViewModel = hiltViewModel(),
    recordDeleteViewModel: RecordDeleteViewModel = hiltViewModel(),
    myPlantViewModel: MyPlantViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    recordViewModel.getAllRecords()
    val recordValues by recordViewModel.recordValues.collectAsStateWithLifecycle()

    val dateFormat = SimpleDateFormat("a HH:mm", Locale.KOREAN)

    val myPlantIds by myPlantViewModel.myPlantIds.observeAsState()
    val myPlantAliases by myPlantViewModel.aliases.observeAsState()

    val expandedStates = remember { mutableStateMapOf<Long, Boolean>() } // 각 카드 상태를 저장

    val handleClickDate: (LocalDate) -> Unit = { selectedDate ->
        expandedStates.clear() // 모든 카드의 상태 초기화
        calendarViewModel.updateSelectedDate(selectedDate)
    }

    val isRecordDeleted by recordDeleteViewModel.recordDeleted.observeAsState()

    LaunchedEffect(isRecordDeleted) {
        recordDeleteViewModel.resetRecordDeletedState()
        recordViewModel.getAllRecords()
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
            durationMillis = 500
        ),
        snapPositionalThreshold = 0.2f
    )

    val selectedDate by calendarViewModel.selectedDate.collectAsState()    // 선택된 날짜
    val totalWeeks = getWeeksOfMonth(selectedDate.year, selectedDate.monthValue, selectedDate.month.maxLength())   // 선택된 달이 몇 주인지
    val weekList = mutableListOf<MutableList<LocalDate>>() // 빈 리스트 초기화, 1주, 2주, 3주, 4주, 5주, 6주를 보관하는 리스트

    // 1일에서 마지막 일까지 n-1주차에 맞게 weekList에 저장
    for (i in 1..selectedDate.lengthOfMonth()) {
        val week = getWeeksOfMonth(selectedDate.year, selectedDate.monthValue, i)
        // 필요한 만큼 리스트 추가
        while (weekList.size < week) {
            weekList.add(mutableListOf())
        }
        // 해당 주차 리스트에 날짜 추가
        weekList[week - 1].add(LocalDate.of(selectedDate.year, selectedDate.monthValue, i))
    }

    // 첫째 주의 빈 칸 채우기 (이전 달 날짜)
    val firstWeekStart = weekList[0].first()
    while (weekList[0].size < 7) {
        weekList[0].add(0, firstWeekStart.minusDays(weekList[0].size.toLong()))
    }

// 마지막 주의 빈 칸 채우기 (다음 달 날짜)
    val lastWeekEnd = weekList[totalWeeks - 1].last()
    while (weekList[totalWeeks - 1].size < 7) {
        weekList[totalWeeks - 1].add(lastWeekEnd.plusDays((weekList[totalWeeks - 1].size - lastWeekEnd.dayOfWeek.value).toLong()))
    }

    LaunchedEffect(Unit, selectedDate) {
        pagerState.animateScrollToPage(
            page = getWeeksOfMonth(selectedDate.year, selectedDate.monthValue, selectedDate.dayOfMonth) -1,
            animationSpec = tween(
                easing = LinearOutSlowInEasing,
                durationMillis = 500
            )
        )
    }


    Box() {
        HorizontalPager(
            state = pagerState,
            flingBehavior = flingBehavior
        ) { page ->
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight / 7f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                userScrollEnabled = true
            ) {
                items(items = weekList[page]) { date ->
                    HorizontalCalendarItem(
                        date = date,
                        selectedDate = selectedDate,
                        enableSelectedMonth = selectedDate.monthValue,
                        onClickDate = handleClickDate
                    )
                }
            }
        }
    }
    LazyColumn(
        modifier = Modifier
            .heightIn(max = screenHeight / 1.6f)
    ) {
        item {
            repeat(recordValues.size) { it ->
                val record = recordValues[it]
                val isExpanded = expandedStates[record.date.time] ?: false // 상태 가져오기

                if (record.date.toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate().isEqual(selectedDate)
                ) {
                    val selectedImageUri by remember(record.image) {
                        mutableStateOf(Uri.parse(record.image))
                    }

                    if (myPlantIds?.contains(record.myPlantId) == true) {
                        ExpandableCard(
                            record.myPlantId,
                            record.title,
                            myPlantAliases!![myPlantIds!!.indexOf(record.myPlantId)],
                            selectedImageUri.toString(),
                            record.content,
                            dateFormat.format(record.date),
                            record.date,
                            expandedState = isExpanded, // 상태 전달
                            onExpandedStateChange = { newState ->
                                expandedStates[record.date.time] = newState // 상태 업데이트
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableCard(
    myPlantId: Long,
    title: String,
    myPlantAlias: String,
    selectedImageUri: String,
    content: String,
    date: String,
    originDate: Date,
    expandedState: Boolean,
    onExpandedStateChange: (Boolean) -> Unit, // 상태 변경 콜백 추가
    recordDeleteViewModel: RecordDeleteViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0x75000000)
        ),
        onClick = {
            onExpandedStateChange(!expandedState) // 상태 변경 콜백 호출
        },
        modifier = Modifier
            .width(screenWidth / 1.25f)
            .wrapContentHeight()
            .padding(5.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 450,
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
                    text = title,
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                )
                IconButton(
                    modifier = Modifier
                        .alpha(0.7f)
                        .rotate(rotationState),
                    onClick = {
                        onExpandedStateChange(!expandedState)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            if (expandedState) {
                if(selectedImageUri != "null") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ImageFromUri(
                            selectedImageUri,
                            Modifier
                                .size(screenWidth / 1.75f)
                                .clip(shape = RoundedCornerShape(16.dp))
                        )
                    }
                }
                Text(
                    text = content,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.gowun_dodum_regular)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if(selectedImageUri != "null") 5.dp else 0.dp)
                        .padding(bottom = 5.dp)
                        .padding(horizontal = 5.dp)
                )
                Text(
                    text = myPlantAlias,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = date,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                    )
                    IconButton(
                        modifier = Modifier
                            .background(Color(0x00FFFFFF), RoundedCornerShape(16.dp))
                            .size(25.dp)
                            .alpha(0.85f),
                        onClick = {
                            recordDeleteViewModel.myPlantId = myPlantId
                            recordDeleteViewModel.title = title
                            recordDeleteViewModel.image = selectedImageUri
                            recordDeleteViewModel.content = content
                            recordDeleteViewModel.date = originDate

                            recordDeleteViewModel.deleteRecord()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Delte",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}
