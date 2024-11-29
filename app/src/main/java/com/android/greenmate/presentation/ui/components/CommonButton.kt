package com.android.greenmate.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.greenmate.R

@Composable
fun CommonButton(
    name: String,
    modifier: Modifier,
    fontSize: TextUnit = 20.sp,
    onClick: () -> Unit
) {
    Box (
        modifier = modifier
            .padding(10.dp, 16.dp),
        contentAlignment = Alignment.Center
    ){
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xE95FA777))
        ) {
            Text(
                text = name,
                fontSize = fontSize,
                fontFamily = FontFamily(Font(R.font.spoqahansansneo_medium)),
                fontWeight = FontWeight.Bold
            )
        }
    }
}