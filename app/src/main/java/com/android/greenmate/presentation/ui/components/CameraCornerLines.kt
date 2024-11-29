package com.android.greenmate.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CameraCornerLines() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Canvas(
        modifier = Modifier
            .size(screenWidth * 0.9f)
            .background(Color(0x25000000), shape = RoundedCornerShape(10.dp))
    ) {
        val strokeWidth = 4.dp.toPx()
        val cornerRadius = 16.dp.toPx()

        // 왼쪽 위 모서리
        drawArc(
            color = Color.Gray,
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
            size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
            style = Stroke(strokeWidth)
        )

        // 오른쪽 위 모서리
        drawArc(
            color = Color.Gray,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(size.width - cornerRadius * 2, 0f),
            size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
            style = Stroke(strokeWidth)
        )

        // 왼쪽 아래 모서리
        drawArc(
            color = Color.Gray,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - cornerRadius * 2),
            size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
            style = Stroke(strokeWidth)
        )

        // 오른쪽 아래 모서리
        drawArc(
            color = Color.Gray,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(size.width - cornerRadius * 2, size.height - cornerRadius * 2),
            size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
            style = Stroke(strokeWidth)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCornerLines() {
    CameraCornerLines()
//    CornerCurves()
}