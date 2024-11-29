package com.android.greenmate.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.greenmate.R

@Composable
fun InputTextField(
    title: MutableState<String>,
    modifier: Modifier,
    imageVector: ImageVector
) {
    BasicTextField(
        value = title.value,
        onValueChange = { title.value = it },        
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.spoqahansansneo_medium)),
        ),
        modifier = modifier.padding(bottom = 5.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .border(2.dp, Color(0xAA009933), RoundedCornerShape(size = 16.dp))
                    .background(Color.White, RoundedCornerShape(size = 16.dp))
                    .padding(all = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = "",
                    tint = Color.DarkGray,
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                innerTextField()
            }
        },
    )
}
