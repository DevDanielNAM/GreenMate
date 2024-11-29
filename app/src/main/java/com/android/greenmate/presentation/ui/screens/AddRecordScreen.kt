package com.android.greenmate.presentation.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.greenmate.R
import com.android.greenmate.presentation.ui.components.CommonButton
import com.android.greenmate.presentation.ui.components.ImageFromUri
import com.android.greenmate.presentation.viewmodel.RecordInputViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    mainNavController: NavHostController,
    myPlantId: String?,
    recordInputViewModel: RecordInputViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var contents by remember { mutableStateOf("") }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickSingleMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
            }
            uri?.let {
                if (myPlantId != null) {
                    recordInputViewModel.image.value = selectedImageUri.toString()
                }
            }
        }

    val backgroundResourceId = context.resources.getIdentifier(
        "main_0",
        "drawable",
        context.packageName
    )

    val backgroundPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(backgroundResourceId)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)  // 캐시 설정
            .build(),
        contentScale = ContentScale.FillHeight
    )

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

    Scaffold(
        containerColor = Color(0x98FFFFFF),
        topBar = {
            TopAppBar(
                colors = TopAppBarColors(Color.Transparent, Color.Transparent, Color.Black, Color.Black, Color.Transparent),
                title = {
                    Text(
                        text = "기록 남기기",
                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                        )
                },
                navigationIcon = {
                    IconButton(onClick = { mainNavController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Stack"
                        )
                    }
                }
            )
        },
    ) {innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                maxLines = 1,
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp)
                    .height(screenWidth / 7),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(size = 16.dp))
                            .fillMaxWidth()
                            .border(2.dp, Color(0xAA009933), RoundedCornerShape(size = 16.dp))
                            .padding(all = 16.dp),
                    ) {
                        innerTextField()
                    }
                },
            )

            BasicTextField(
                value = contents,
                onValueChange = { contents = it },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.gowun_dodum_regular)),
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight()
                    .heightIn(min = screenWidth / 2, max = screenHeight / 1.75f),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(size = 16.dp))
                            .fillMaxWidth()
                            .border(2.dp, Color(0xAA009933), RoundedCornerShape(size = 16.dp))
                            .padding(all = 16.dp),
                    ) {
                        innerTextField()
                    }
                },
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(screenWidth / 4.5f)
                        .padding(horizontal = 16.dp)
                ) {
                    if (selectedImageUri != null) {
                        Box {
                            Box(
                                modifier = Modifier.size(screenWidth / 4.5f).zIndex(2f).padding(3.dp),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                IconButton(
                                    modifier = Modifier.size(20.dp),
                                    onClick = { selectedImageUri = null}
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = null,
                                        modifier = Modifier.background(Color(0x75000000)),
                                        tint = Color.White
                                    )
                                }
                            }
                            ImageFromUri(
                                imageUri = selectedImageUri!!.toString(),
                                modifier = Modifier
                                    .size(screenWidth / 4.5f)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                        AddRecordButton(
                            name = "사진 변경",
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .padding(vertical = 5.dp),
                            buttonModifier = Modifier
                                .fillMaxWidth(1f)
                                .fillMaxHeight(),
                            Color(0xC4EC407A),
                            fontSize = 25.sp,
                        ) {
                            pickSingleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    } else {
                        AddRecordButton(
                            name = "사진 추가",
                            modifier = Modifier
                                .padding(vertical = 5.dp),
                            buttonModifier = Modifier
                                .fillMaxWidth(1f)
                                .height(50.dp),
                            Color(0xE95FA777),
                            fontSize = 20.sp
                        ) {
                            pickSingleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }
                }

                AddRecordButton(
                    name = "기록 남기기",
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    buttonModifier = Modifier
                        .fillMaxWidth(1f)
                        .height(50.dp),
                    Color(0xE95FA777),
                    fontSize = 20.sp
                ) {
                    if (myPlantId != null) {
                        recordInputViewModel.myPlantId.value = myPlantId.toLong()
                    }
                    recordInputViewModel.title.value = title
                    recordInputViewModel.content.value = contents
                    recordInputViewModel.date.value = Date()
                    recordInputViewModel.image.value = selectedImageUri.toString()
                    recordInputViewModel.insertRecord()
                    mainNavController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun AddRecordButton(
    name: String,
    modifier: Modifier,
    buttonModifier: Modifier,
    containerColor: Color,
    fontSize: TextUnit = 20.sp,
    onClick: () -> Unit
) {
    Box (
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor)
        ) {
            Text(
                text = name,
                fontSize = fontSize,
                fontFamily = FontFamily(Font(R.font.dohyeon_regular))
            )
        }
    }
}