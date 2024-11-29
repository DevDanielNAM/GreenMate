package com.android.greenmate.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.greenmate.R

@Composable
fun AddSpaceQuestion(title: String, description: String) {
    Column (

    ){
        Text(
            modifier = Modifier.padding(16.dp, 0.dp),
            text = title,
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.padding(18.dp, 5.dp),
            text = description,
            fontFamily = FontFamily(Font(R.font.spoqahansansneo_medium)),
            fontSize = 12.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddSpaceQuestionPreview() {
    AddSpaceQuestion("햇빛이 얼마나 드나요?", "햇빛에 따라 식물 관리법이 달라져요")
}