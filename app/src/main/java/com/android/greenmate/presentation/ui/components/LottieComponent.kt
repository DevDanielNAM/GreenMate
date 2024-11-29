package com.android.greenmate.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieComponent(fileName: String, modifier: Modifier) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(fileName, "raw", context.packageName)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resourceId))

    LottieAnimation(
        composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier.fillMaxSize()
    )
}