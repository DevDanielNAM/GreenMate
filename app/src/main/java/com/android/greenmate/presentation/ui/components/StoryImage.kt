package com.android.greenmate.presentation.ui.components

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StoryImage(pagerState: PagerState, showAlarmStoryDialog: MutableState<Boolean>, deltaY: MutableFloatState, onTap: (Boolean) -> Unit, content: @Composable (Int) -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
        modifier = Modifier
            .width(screenWidth)
            .background(Color.Transparent)
            .fillMaxHeight()
            .pointerInput(Unit) {

                detectDragGestures(
                    onDragEnd = {
                        deltaY.floatValue = 0f
                        onTap(false)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume() // 터치 이벤트 처리 완료
                        val (x, y) = dragAmount

                        if (deltaY.floatValue <= 0f && y < 0) {
                            deltaY.floatValue = 0f
                            onTap(false)
                        } else {
                            deltaY.floatValue += y
                        }

                        if (y < -60) {
                            deltaY.floatValue = 0f
                            onTap(false)
                        }
                        if (y > 100) showAlarmStoryDialog.value = false
                    }
                )
            }
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onTap(true)
                    }

                    MotionEvent.ACTION_UP -> {
                        onTap(false)
                    }
                }
                true
            }
    ) {
        if (deltaY.floatValue > 550) {
            showAlarmStoryDialog.value = false
        }
        content(it)
    }
}