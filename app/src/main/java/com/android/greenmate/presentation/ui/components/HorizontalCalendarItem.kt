package com.android.greenmate.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.greenmate.R
import com.android.greenmate.presentation.viewmodel.RecordDeleteViewModel
import com.android.greenmate.presentation.viewmodel.RecordViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@Composable
fun HorizontalCalendarItem(
    date: LocalDate,
    selectedDate: LocalDate,
    enableSelectedMonth: Int,
    onClickDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    recordViewModel: RecordViewModel = hiltViewModel(),
    recordDeleteViewModel: RecordDeleteViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val isRecordDeleted by recordDeleteViewModel.recordDeleted.observeAsState()

    val recordState = remember(date) { mutableStateOf(false) }

    LaunchedEffect(isRecordDeleted, date) {
        recordDeleteViewModel.resetRecordDeletedState()
        recordState.value = recordViewModel.checkRecordExistsForDateSync(date)
        recordViewModel.getAllRecords()
    }

    Box() {
        Box(
            modifier = Modifier
                .size(40.dp)
                .zIndex(2f)
                .fillMaxSize()
        ) {
            if (recordState.value) {
                Image(
                    painter = painterResource(id = R.drawable.checked),
                    contentDescription = "check"
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp)
                .padding(top = 10.dp)
                .height(screenHeight)
        ) {
            Column(
                modifier = modifier
                    .padding(vertical = 5.dp)
                    .width(40.dp)
                    .height(80.dp)
                    .background(
                        if (date.isEqual(selectedDate)) Color(0xE626A69A)
                        else Color(0xC7FFFFFF),
                        RoundedCornerShape(16.dp)
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                    color = if (date.isEqual(selectedDate)) Color(0xFFFFFFFF) else Color.Gray
                )
                Spacer(modifier = Modifier.size(8.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (date.monthValue == enableSelectedMonth) {
                                    onClickDate(date)
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (date.isEqual(selectedDate)) Color.White else {
                            if (date.monthValue == enableSelectedMonth) Color.Gray else Color.LightGray
                        },
                    )
                }
            }
        }
    }
}


