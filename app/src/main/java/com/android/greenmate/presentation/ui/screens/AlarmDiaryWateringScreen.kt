package com.android.greenmate.presentation.ui.screens

import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.greenmate.R
import com.android.greenmate.presentation.ui.components.AddSpaceQuestion
import com.android.greenmate.presentation.ui.components.HorizontalCalendar
import com.android.greenmate.presentation.ui.components.ImageFromUri
import com.android.greenmate.presentation.ui.components.TodayButton
import com.android.greenmate.presentation.viewmodel.CalendarViewModel
import com.android.greenmate.presentation.viewmodel.RecordViewModel
import com.android.greenmate.utils.getWeeksOfMonth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@Composable
fun AlarmDiaryWateringScreen(
    mainPagerState: PagerState,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    recordViewModel: RecordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val recordValues by recordViewModel.recordValues.collectAsStateWithLifecycle()

    val selectedDate by calendarViewModel.selectedDate.collectAsState()
    val totalWeeks = getWeeksOfMonth(selectedDate.year, selectedDate.monthValue, selectedDate.month.maxLength())
    val selectedWeeks = getWeeksOfMonth(selectedDate.year, selectedDate.monthValue, selectedDate.dayOfMonth)
    val weekList = mutableListOf<MutableList<LocalDate>>() // 빈 리스트 초기화

    for (i in 1..selectedDate.lengthOfMonth()) {
        val week = getWeeksOfMonth(selectedDate.year, selectedDate.monthValue, i)
        // 필요한 만큼 리스트 추가
        while (weekList.size < week) {
            weekList.add(mutableListOf())
        }
        // 해당 주차 리스트에 날짜 추가
        weekList[week - 1].add(LocalDate.of(selectedDate.year, selectedDate.monthValue, i))
    }

    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = {weekList.size}, initialPage = selectedWeeks-1)

    var onClickedTodayButton by remember { mutableStateOf(false) }

    val backgroundResourceId = context.resources.getIdentifier(
        "main_0",
        "drawable",
        context.packageName
    )

    val backgroundPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(backgroundResourceId)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .build(),
        contentScale = ContentScale.FillHeight
    )

    LaunchedEffect(mainPagerState.currentPage != 1) {
        calendarViewModel.initDateToToday()
        delay(300)
        onClickedTodayButton = true
        pagerState.animateScrollToPage(
            page = getWeeksOfMonth(
                selectedDate.year,
                selectedDate.monthValue,
                selectedDate.dayOfMonth
            ) - 1,
            animationSpec = spring(stiffness = 1000f)
        )
        onClickedTodayButton = false
    }

    LaunchedEffect(recordValues) {
        recordViewModel.getAllRecords()
    }

    // Background Image
    Box(
        modifier = Modifier.height(screenHeight)
    ) {
        Image(
            painter = backgroundPainter,
            contentDescription = "",
            contentScale = ContentScale.FillHeight,
        )
    }

    Column(
        modifier = Modifier
            .background(Color(0x75FCF8F7))
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp)
            .height(screenHeight),
    ) {
        Text(
            text = "주인님 일지",
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            fontSize = 40.sp,
            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
            textAlign = TextAlign.Center
        )
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
        ) {
            YearDropdownMenu(
                selectedDate = selectedDate,
                onYearSelected = { year ->
                    calendarViewModel.updateSelectedDate(selectedDate.withYear(year))
                }
            )

            MonthDropdownMenu(selectedDate) { month ->
                calendarViewModel.updateSelectedDate(selectedDate.withMonth(month))
            }

            TodayButton(
                modifier = Modifier.padding(end = 10.dp),
                onClickTodayButton = {
                    coroutineScope.launch {
                        calendarViewModel.initDateToToday()
                        onClickedTodayButton = true

                        pagerState.animateScrollToPage(
                            page = getWeeksOfMonth(
                                selectedDate.year,
                                selectedDate.monthValue,
                                selectedDate.dayOfMonth
                            ) - 1,
                            animationSpec = spring(stiffness = 1000f)
                        )

                        onClickedTodayButton = false
                    }
                }
            )
        }

        HorizontalCalendar(
            pagerState = pagerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearDropdownMenu(
    selectedDate: LocalDate,
    onYearSelected: (Int) -> Unit,
    recordViewModel: RecordViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val records by recordViewModel.recordValues.collectAsStateWithLifecycle()

    recordViewModel.getAllRecords()

    var expanded by remember { mutableStateOf(false) }

    val currentYear = LocalDate.now().year
    val oldestYear = records.minOfOrNull {
        it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().year
    } ?: currentYear
    val years = (oldestYear..currentYear).toList()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .width(screenWidth / 2.5f)
            .background(Color.Transparent)
    ) {
        TextField(
            value = "${selectedDate.year}년",
            onValueChange = {},
            readOnly = true, // 직접 입력이 아닌 드롭다운 메뉴로 선택
            modifier = Modifier
                .background(Color.Transparent)
                .menuAnchor(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                textAlign = TextAlign.Center
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Drop-Down Arrow"
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .wrapContentHeight()
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text("${year}년") },
                    onClick = {
                        onYearSelected(year) // 선택된 연도를 콜백으로 전달
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthDropdownMenu(
    selectedDate: LocalDate,
    onMonthSelected: (Int) -> Unit // 선택된 월을 전달하는 콜백
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var expanded by remember { mutableStateOf(false) }
    val months = (1..12).map { it.toString().padStart(2, '0') + "월" } // 01월, 02월... 12월

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .width(screenWidth / 3.0f)
            .background(Color.Transparent)
    ) {
        TextField(
            value = selectedDate.monthValue.toString().padStart(2, '0') + "월",
            onValueChange = {},
            readOnly = true, // 직접 입력이 아닌 드롭다운 메뉴로 변경
            modifier = Modifier
                .background(Color.Transparent)
                .menuAnchor(), // 메뉴와 텍스트 필드 연결
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                textAlign = TextAlign.Center
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Drop-Down Arrow"
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .height(screenWidth / 1.4f)
        ) {
            months.forEachIndexed { index, month ->
                DropdownMenuItem(
                    text = { Text(month) },
                    onClick = {
                        onMonthSelected(index + 1) // 선택된 월을 콜백으로 전달
                        expanded = false
                    }
                )
            }
        }
    }
}