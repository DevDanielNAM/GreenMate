package com.android.greenmate.presentation.ui.screens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Locale
import coil.compose.rememberAsyncImagePainter
import com.android.greenmate.R
import com.android.greenmate.presentation.ui.components.CameraCornerLines
import com.android.greenmate.presentation.ui.components.CheckMultiplePermissions
import com.android.greenmate.presentation.ui.components.CommonButton
import com.android.greenmate.presentation.ui.components.LottieComponent
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddPlantByCamera(
    mainPlantNavController: NavHostController,
    addOrDisease: String?
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraPermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var savedUri by remember { mutableStateOf<Uri?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var captureJob by remember { mutableStateOf<Job?>(null) }
    var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var cameraInfo by remember { mutableStateOf<CameraInfo?>(null) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(true) }

    // 줌 슬라이더 상태
    var zoomRatio by remember { mutableStateOf(1f) }
    // 슬라이더 가시성 상태
    var showSlider by remember { mutableStateOf(false) }

    // 이미지 미리보기 상태
    var imagePreviewUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePreview by remember { mutableStateOf(false) }

    val permissionsList = listOfNotNull(
        android.Manifest.permission.CAMERA
    )
    val permissionState = rememberMultiplePermissionsState(permissions = permissionsList)
    val showPermissionDialog = remember { mutableStateOf(false) }


    // 이미지 삭제를 위한 유틸리티 함수
    fun deleteImageFile(uri: Uri?) {
        uri?.let {
            context.contentResolver.delete(it, null, null)
        }
    }

    // 핀치 제스처 감지 시 슬라이더 가시성 관리
    LaunchedEffect(showSlider) {
        if (showSlider) {
            delay(2000)  // 2초 후 슬라이더 숨김
            showSlider = false
        }
    }

    LaunchedEffect(cameraPermissionState) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        val cameraProvider = cameraProviderFuture.get()
        val previewView = remember { PreviewView(context) }
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        LaunchedEffect(cameraProvider) {
            try {
                cameraProvider.unbindAll()

                // ImageCapture를 초기화하고 바인딩
                imageCapture = ImageCapture.Builder().setFlashMode(flashMode).build()

                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                cameraControl = camera.cameraControl
                cameraInfo = camera.cameraInfo

                // 카메라의 초기 줌 비율 설정
                zoomRatio = cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
            } catch (e: Exception) {
                Log.e("CameraScreen", "Camera initialization failed", e)
            }
        }


        LaunchedEffect(Unit) {
            delay(800)
            isLoading = true
        }

        if (showImagePreview) {
            // 이미지 미리보기 화면
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    imagePreviewUri?.let { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Captured Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CommonButton(name = "다시 촬영", modifier = Modifier.width(screenWidth * 0.45f)) {
                            // 사용자 선택: 다시 촬영
                            showImagePreview = false
                            isCapturing = false
                            flashMode = ImageCapture.FLASH_MODE_OFF
                            deleteImageFile(savedUri)
                        }
                        CommonButton(name = "식물 분석", modifier = Modifier.width(screenWidth * 0.45f)) {
                            // 사용자 선택: 다음 단계로 진행
                            imagePreviewUri?.let { uri ->
                                if(addOrDisease == "add") {
                                    mainPlantNavController.navigate("inference/${Uri.encode(uri.toString())}")
                                } else {
                                    mainPlantNavController.navigate(
                                        "disease_inference/${
                                            Uri.encode(
                                                uri.toString()
                                            )
                                        }"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        else if(!isLoading){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .height(250.dp)
                        .width(300.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LottieComponent(fileName = "loading", modifier = Modifier)
                    }
                }
                Text(
                    modifier = Modifier.padding(vertical = 10.dp),
                    text = "조금만 기다려 주세요",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Box(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        val currentZoomRatio = cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                        val newZoomRatio = (currentZoomRatio * zoom).coerceIn(
                            cameraInfo?.zoomState?.value?.minZoomRatio ?: 1f,
                            cameraInfo?.zoomState?.value?.maxZoomRatio ?: 1f
                        )
                        cameraControl?.setZoomRatio(newZoomRatio)
                        zoomRatio = newZoomRatio
                        showSlider = true
                    }
                }
            ) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )

                // Overlaying elements
                Box(modifier = Modifier.fillMaxSize()) {
                    if(isCapturing){
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.White)
                                .zIndex(3f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier.padding(vertical = 10.dp),
                                text = "사진 촬영 중이에요",
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CameraCornerLines()
                    }

                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if(showSlider) {// Zoom Slider
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Slider(
                                        value = zoomRatio,
                                        onValueChange = { value ->
                                            zoomRatio = value
                                            cameraControl?.setZoomRatio(zoomRatio)
                                        },
                                        valueRange = cameraInfo?.zoomState?.value?.minZoomRatio?.let { minZoom ->
                                            cameraInfo?.zoomState?.value?.maxZoomRatio?.let { maxZoom ->
                                                minZoom..maxZoom
                                            }
                                        } ?: 1f..1f, // 기본 범위는 1f..1f로 설정
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                    )
                                }
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors(Color.Transparent),
                                onClick = {
                                    flashMode = if (flashMode == ImageCapture.FLASH_MODE_ON) {
                                        ImageCapture.FLASH_MODE_OFF
                                    } else {
                                        ImageCapture.FLASH_MODE_ON
                                    }
                                    imageCapture?.flashMode = flashMode // 플래시 모드 업데이트
                                }) {
                                if (flashMode == ImageCapture.FLASH_MODE_ON) {
                                    Icon(Icons.Filled.FlashOn, contentDescription = "Flash On")
                                } else {
                                    Icon(Icons.Filled.FlashOff, contentDescription = "Flash Off")
                                }
                            }
                        }

                        // Bottom buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp, 16.dp),
//                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RecentImageIconButton(mainPlantNavController, addOrDisease)


                            IconButton(
                                onClick = {
                                    // 촬영 중이면 아무 작업도 하지 않음
                                    if (isCapturing) return@IconButton

                                    // 촬영 상태를 true로 설정
                                    isCapturing = true

                                    captureJob?.cancel()

                                    captureJob = CoroutineScope(Dispatchers.Main).launch {
                                        val captureImage = imageCapture ?: return@launch

                                        val name = "greenmate-" + SimpleDateFormat(
                                            "yyMMdd_HHmmss_SSS",
                                            Locale.KOREA
                                        ).format(System.currentTimeMillis())

                                        val contentValues = ContentValues().apply {
                                            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                put(
                                                    MediaStore.Images.Media.RELATIVE_PATH,
                                                    "Pictures/GreenMate-Images"
                                                )
                                            }
                                        }

                                        captureImage.takePicture(
                                            ContextCompat.getMainExecutor(context),
                                            object : ImageCapture.OnImageCapturedCallback() {
                                                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                                    val bitmap = imageProxy.toBitmap()
                                                    imageProxy.close()
                                                    // 이미지 회전 조정
                                                    val rotationDegrees =
                                                        imageProxy.imageInfo.rotationDegrees
                                                    val rotatedBitmap =
                                                        rotateBitmap(bitmap, rotationDegrees)

                                                    // 정사각형으로 자르기
                                                    val squareBitmap =
                                                        cropToSquare(rotatedBitmap)

                                                    saveBitmapToGallery(
                                                        context,
                                                        squareBitmap,
                                                        contentValues
                                                    ) { uri ->
                                                        savedUri = uri
                                                        imagePreviewUri = uri
                                                        showImagePreview = true

                                                        // 성공 메시지 표시
                                                        Toast.makeText(
                                                            context,
                                                            "사진이 저장되었어요",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    isCapturing = false
                                                    flashMode = ImageCapture.FLASH_MODE_OFF
                                                }

                                                override fun onError(exception: ImageCaptureException) {
                                                    isCapturing = false
                                                    // 실패 메시지 표시
                                                    Toast.makeText(
                                                        context,
                                                        "촬영 실패",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                    Log.e(
                                                        "CameraScreen",
                                                        "Photo capture failed: ${exception.message}",
                                                        exception
                                                    )
                                                }
                                            }
                                        )
                                    }
                                },
                                enabled = !isCapturing,
                                modifier = Modifier
                                    .background(Color.White, shape = CircleShape)
                                    .size(65.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = "Capture Image",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(58.dp)
                                        .border(2.dp, Color.Black, shape = CircleShape)
                                )
                            }


                            IconButton(
                                onClick = {
                                    showDialog = true
                                }
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        painter = rememberVectorPainter(Icons.Outlined.Info),
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
        if (showDialog && isLoading) {
            AlertDialog(
                onDismissRequest = { showDialog = false }, // 팝업 외부 클릭 시 닫기
                containerColor = Color(0xE9FFFFFF),
                text = {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(
                            text = "가운데 가이드 박스 안에 식물을 위치시키면 인식이 더 잘 돼요",
                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                            fontSize = 20.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier
                                .padding(bottom = 20.dp, start = 5.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CameraCornerLines()
                            LottieComponent(fileName = "plant", modifier = Modifier
                                .width(screenWidth * 0.6f)
                                .zIndex(2f))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        onClick = { showDialog = false }
                    ) {
                        Text(
                            text = "확인",
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                            fontSize = 15.sp
                            )
                    }
                }
            )
        }
    } else {
        showPermissionDialog.value = true
        HandlePermissionActions(permissionState = permissionState, showPermissionDialog = showPermissionDialog, mainPlantNavController)
    }
}


@Composable
fun RecentImageIconButton(
    mainPlantNavController: NavHostController,
    addOrDisease: String?
) {
    val context = LocalContext.current
    var recentImageUri by remember { mutableStateOf<Uri?>(null) }
    if (addOrDisease != null) {
        Log.d("addordisease", addOrDisease)
    }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickSingleMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
            }
            uri?.let {
                if(addOrDisease == "add") {
                    mainPlantNavController.navigate("inference/${Uri.encode(uri.toString())}")
                } else {
                    mainPlantNavController.navigate(
                        "disease_inference/${
                            Uri.encode(
                                uri.toString()
                            )
                        }"
                    )
                }
            }
        }

    LaunchedEffect(Unit) {
        recentImageUri = getLatestImageUri(context)
    }

    IconButton(
        modifier = Modifier
            .size(60.dp)
            .border(0.dp, Color.Transparent, shape = RoundedCornerShape(10.dp))
            .background(Color.Transparent, shape = RoundedCornerShape(10.dp)),
        onClick = {
            mainPlantNavController.navigate("gallery/${Uri.encode(addOrDisease)}")
//            pickSingleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }) {
        if (recentImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(recentImageUri),
                contentDescription = "Recent Image",
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(40.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                shape = CircleShape,
                color = Color.Gray,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Open Gallery",
                    tint = Color.White
                )
            }
        }
    }
}

// Helper function to convert ImageProxy to Bitmap
fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

// Helper function to rotate a Bitmap
fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}


// Helper function to crop a Bitmap to a square
fun cropToSquare(bitmap: Bitmap): Bitmap {
    val dimension = min(bitmap.width, bitmap.height)
    val widthOffset = (bitmap.width - dimension) / 2
    val heightOffset = (bitmap.height - dimension) / 2
    return Bitmap.createBitmap(bitmap, widthOffset, heightOffset, dimension, dimension)
}

// Helper function to save Bitmap to the gallery
fun saveBitmapToGallery(context: Context, bitmap: Bitmap, contentValues: ContentValues, onSuccess: (Uri) -> Unit) {
    val uri =
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            onSuccess(uri)
        }
    }
}

fun getLatestImageUri(context: Context): Uri? {
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
        if (cursor.moveToFirst()) {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val id = cursor.getLong(idColumn)
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
        }
    }
    return null
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun HandlePermissionActions(
    permissionState: MultiplePermissionsState,
    showPermissionDialog: MutableState<Boolean>,
    bottomNavController: NavHostController
) {
    if (showPermissionDialog.value) {
        CheckMultiplePermissions(
            permissionState = permissionState,
            onPermissionResult = { if (it) showPermissionDialog.value = false },
            showPermissionDialog = showPermissionDialog
        )
    } else {
        showPermissionDialog.value = false
        bottomNavController.popBackStack()
    }
}