package com.android.greenmate.presentation.ui.components

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.greenmate.R


@SuppressLint("SuspiciousIndentation")
@Composable
fun ImageFromUri(imageUri: String, modifier: Modifier) {
    val uri = Uri.parse(imageUri)

    val model = ImageRequest
            .Builder(LocalContext.current)
            .data(uri)
            .crossfade(true)
            .crossfade(800)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .build()

        AsyncImage(
            model = model,
            contentDescription = "Image",
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.no_image) // 에러 시 보여줄 이미지
        )
}