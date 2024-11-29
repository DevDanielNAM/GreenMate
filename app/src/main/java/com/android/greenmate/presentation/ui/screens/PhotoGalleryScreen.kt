package com.android.greenmate.presentation.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.android.greenmate.R
import com.android.greenmate.presentation.navigation.CameraNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(
    mainPlantNavController: NavHostController,
    addOrDisease: String?
) {
    val context = LocalContext.current
    var imageUris by remember { mutableStateOf(listOf<Uri>()) }

    LaunchedEffect(Unit) {
        imageUris = getAllImagesFromAppFolder(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "분석할 식물 선택",
                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { mainPlantNavController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Stack"
                        )
                    }
                },
            )
        },
    ){innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(imageUris.size) { index ->
                Image(
                    painter = rememberAsyncImagePainter(imageUris[index]),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            onClick = {
                                if (addOrDisease == "add") {
                                    mainPlantNavController.navigate(
                                        "inference/${
                                            Uri.encode(
                                                imageUris[index].toString()
                                            )
                                        }"
                                    ) {
                                        popUpTo(CameraNavItem.Gallery.route) {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    mainPlantNavController.navigate(
                                        "disease_inference/${
                                            Uri.encode(
                                                imageUris[index].toString()
                                            )
                                        }"
                                    ) {
                                        popUpTo(CameraNavItem.Gallery.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }

    }

}

fun getAllImagesFromAppFolder(context: Context): List<Uri> {
    val imageUris = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val query = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val uri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )
            imageUris.add(uri)
        }
    }
    return imageUris
}
