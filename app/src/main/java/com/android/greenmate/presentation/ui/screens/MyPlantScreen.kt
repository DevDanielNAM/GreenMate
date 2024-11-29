package com.android.greenmate.presentation.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BlurMaskFilter
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChangeCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.greenmate.R
import com.android.greenmate.data.ble.BleManager
import com.android.greenmate.data.datasource.local.entity.AlarmEntity
import com.android.greenmate.domain.BleInterface
import com.android.greenmate.domain.model.DeviceData
import com.android.greenmate.domain.model.Module
import com.android.greenmate.presentation.navigation.MainPlantNavItem
import com.android.greenmate.presentation.scheduledAlarm
import com.android.greenmate.presentation.ui.components.CommonButton
import com.android.greenmate.presentation.ui.components.LinearIndicator
import com.android.greenmate.presentation.ui.components.LottieComponent
import com.android.greenmate.presentation.ui.components.ModulePreviewChart
import com.android.greenmate.presentation.ui.components.StoryImage
import com.android.greenmate.presentation.ui.components.removeAlarm
import com.android.greenmate.presentation.viewmodel.AlarmViewModel
import com.android.greenmate.presentation.viewmodel.ModuleInputViewModel
import com.android.greenmate.presentation.viewmodel.ModuleViewModel
import com.android.greenmate.presentation.viewmodel.MyPlantDeleteViewModel
import com.android.greenmate.presentation.viewmodel.MyPlantUpdateViewModel
import com.android.greenmate.presentation.viewmodel.MyPlantViewModel
import com.android.greenmate.presentation.viewmodel.PlantViewModel
import com.android.greenmate.presentation.viewmodel.RecordInputViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun MyPlantScreen(
    mainPlantNavController: NavHostController,
    bleManager: BleManager,
    plantViewModel: PlantViewModel = hiltViewModel(),
    myPlantViewModel: MyPlantViewModel = hiltViewModel(),
    myPlantUpdateViewModel: MyPlantUpdateViewModel = hiltViewModel(),
    myPlantDeleteViewModel: MyPlantDeleteViewModel = hiltViewModel(),
    moduleViewModel: ModuleViewModel = hiltViewModel(),
    moduleInputViewModel: ModuleInputViewModel = hiltViewModel(),
    recordInputViewModel: RecordInputViewModel = hiltViewModel(),
    alarmViewModel: AlarmViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val hazeState = remember { HazeState() }
    val hazeStateConnected = remember { HazeState() }

    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    val isConnecting = remember { mutableStateOf(bleManager.isDeviceConnected()) }

    val decimalFormat = DecimalFormat("#")
    decimalFormat.roundingMode.apply { RoundingMode.HALF_UP }

    myPlantViewModel.getFavoriteMyPlant()
    myPlantViewModel.getAllMyPlants()

    val plantId by myPlantViewModel.favoritePlantId.observeAsState()
    val myPlantId by myPlantViewModel.favoriteMyPlantId.observeAsState()
    val myPlantCategory by myPlantViewModel.favoriteMyPlantCategory.observeAsState()
    val myPlantAlias by myPlantViewModel.favoriteMyPlantAlias.observeAsState()
    val myPlantImage by myPlantViewModel.favoriteMyPlantImage.observeAsState()
    val myPlantDate by myPlantViewModel.favoriteMyPlantDate.observeAsState()

    val myPlantIds by myPlantViewModel.myPlantIds.observeAsState()
    val myPlantAliases by myPlantViewModel.aliases.observeAsState()
    val myPlantImages by myPlantViewModel.myPlantImages.observeAsState()
    val myPlantFavorites by myPlantViewModel.favorites.observeAsState()

    val isMyPlantDelted by myPlantDeleteViewModel.plantDeleted

    plantId?.let { plantViewModel.getPlantInfoById(it) }

    val plantLight by plantViewModel.light.observeAsState()
    val plantTemperature by plantViewModel.temperature.observeAsState()
    val plantHumidity by plantViewModel.humidity.observeAsState()
    val plantWater by plantViewModel.water.observeAsState()

    val resourceId = context.resources.getIdentifier(
        myPlantImage ?: "no_image",
        "drawable", context.packageName)

    val duration = myPlantDate.takeIf { it != Date() }?.let { getDayDifferenceText(it) }

    myPlantId?.let { moduleViewModel.getModulesByMyPlantId(it) }

    val moduleValues by moduleViewModel.moduleValues.observeAsState(emptyList())
    val soilMoistures by moduleViewModel.soilMoistures.observeAsState(emptyList())

    var light by remember { mutableFloatStateOf(0.0f) }
    var temperature by remember { mutableFloatStateOf(0.0f) }
    var humidity by remember { mutableFloatStateOf(0.0f) }
    var soilMoisture by remember { mutableFloatStateOf(0.0f) }

    val sharedPreferences = context.getSharedPreferences("ble_prefs", Context.MODE_PRIVATE)
    var deviceData = loadBle(sharedPreferences, myPlantId.toString())
    var isModuleValueLoaded by remember { mutableStateOf(false) }

    var showAddPlantDialog by remember { mutableStateOf(false) }
    var showEditAliasDialog by remember { mutableStateOf(false) }
    var showChangeMyPlantDialog by remember { mutableStateOf(false) }
    var showWateringDialog by remember { mutableStateOf(false) }
    val text = remember { mutableStateOf("") }

    var isWatering by remember { mutableStateOf(false) }
    var wateringStartMoisture by remember { mutableStateOf(0f) }
    var lastMoistureChange by remember { mutableStateOf(0L) }

    val alarms by alarmViewModel.alarms.observeAsState(emptyList())
    myPlantId?.let { alarmViewModel.loadAlarms(it) }
    var myPlantAlarmColor = Color(0xFFDDDDF1)

    val category = when(myPlantCategory) {
        "leaf" -> "main_leaf"
        "flower" -> "main_flower"
        "cactus" -> "main_cactus"
        "fruit" -> "main_fruit"
        else -> {"main_0"}
    }

    val backgroundResourceId = context.resources.getIdentifier(
        category,
        "drawable",
        context.packageName
    )

    val backgroundPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(backgroundResourceId)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .build(),
        contentScale = ContentScale.FillHeight
    )

    if(myPlantId != null) EveryDayAlarm(myPlantId , myPlantAlias)

    LaunchedEffect(Unit, myPlantId, plantId, isMyPlantDelted) {
        myPlantViewModel.getFavoriteMyPlant()
        myPlantViewModel.getAllMyPlants()
        delay(100)
        plantId.takeIf { it != 0L }?.let { plantViewModel.getPlantInfoById(it) }
        myPlantId.takeIf { it != 0L }?.let {
            moduleViewModel.getModulesByMyPlantId(it)
            alarmViewModel.loadAlarms(it)
        }
        deviceData = loadBle(sharedPreferences, myPlantId.toString())
    }


    LaunchedEffect(myPlantId, deviceData.name.isNotBlank()) {
        myPlantViewModel.getFavoriteMyPlant()

        if (!isConnecting.value && deviceData.name.isNotBlank()) {
            bleManager.startBleConnectGatt(deviceData)  // BLE 연결 시도
        }
    }

    LaunchedEffect(isConnecting.value) {
        var lastSavedTime = System.currentTimeMillis() // 마지막 저장 시간을 추적
        var isFirstSave = true
        if(!isConnecting.value) {
            isModuleValueLoaded = false
            //return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            bleManager.onConnectedStateObserve(object : BleInterface {
                override fun onConnectedStateObserve(isConnected: Boolean, data: String) {
                    Log.d("BLE", "isConnected: $isConnected, data: $data")
                    isConnecting.value = isConnected
                }

                override fun onSensorValueChanged(sensorType: String, value: Float) {
                    when (sensorType) {
                        "Light" -> {
                            light = value
                            moduleInputViewModel.lightIntensity = light
                        }

                        "Temperature" -> {
                            temperature = value
                            moduleInputViewModel.temperature = temperature
                        }

                        "Humidity" -> {
                            humidity = value
                            moduleInputViewModel.humidity = humidity
                        }

                        "Soil Moisture" -> {
                            soilMoisture = value
                            moduleInputViewModel.soilMoisture = soilMoisture
                        }
                    }
                    moduleInputViewModel.timestamp = Date()
                    if (myPlantId != null) {
                        moduleInputViewModel.myPlantId = myPlantId!!
                    }
                    // 모듈 연결 시 딜레이로 인한 0 값 제외
                    if (light != 0f && temperature != 0f && humidity != 0f && soilMoisture != 0f) {
                        isModuleValueLoaded = true
                        val currentTime = System.currentTimeMillis()

                        if (isFirstSave) {
                            // 최초에는 즉시 저장
                            Log.d("Module_insert", "First insert")
                            moduleInputViewModel.insertModule()
                            isFirstSave = false // 첫 번째 저장 후 플래그를 false로 설정
                            lastSavedTime = currentTime // 마지막 저장 시간 업데이트
                        } else if (currentTime - lastSavedTime >= 10 * 1000) {
                            Log.d("Module_insert", "insert")
                            moduleInputViewModel.insertModule()
                            lastSavedTime = currentTime // 마지막 저장 시간 업데이트
                        }
                    }
                }
            })
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        isLoading = false
    }

    LaunchedEffect(myPlantAlias, myPlantFavorites) {
        text.value = myPlantAlias ?: ""
    }

    LaunchedEffect(light, temperature, humidity, soilMoisture) {
        myPlantId?.let {
            moduleViewModel.getModulesByMyPlantId(it)
        }
    }

    fun detectWatering(currentMoisture: Float) {
        val moistureDiff = moduleValues.last().soilMoisture - currentMoisture

        when {
            // 물 주기 시작 감지
            !isWatering && moistureDiff > 10 -> {
                isWatering = true
                wateringStartMoisture = currentMoisture
                showWateringDialog = true
                lastMoistureChange = System.currentTimeMillis()
            }

            // 물 주기 중단 감지 (30초 동안 수분 변화가 없을 때)
            isWatering && System.currentTimeMillis() - lastMoistureChange > 30000 -> {
                isWatering = false
                if (!plantWateringFullLess(plantWater!!)[0].contains(calcSoilMoisture(currentMoisture))) {
                    showWateringDialog = true
                }
            }

            // 물 주기 진행 중 수분 변화 업데이트
            isWatering && moistureDiff > 2 -> {
                lastMoistureChange = System.currentTimeMillis()
            }
        }
    }

    // 물주기 감지
    if(moduleValues.isNotEmpty() && soilMoisture > 0) {
        detectWatering(soilMoisture)
        if(showWateringDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (!plantWateringFullLess(plantWater!!)[0].contains(calcSoilMoisture(soilMoisture))) {
                        showWateringDialog = false
                        // 5초 후 다시 다이얼로그 표시
                        Handler(Looper.getMainLooper()).postDelayed({
                            showWateringDialog = true
                        }, 5000)
                    } else {
                        isWatering = false
                        showWateringDialog = false
                    }
                },
                containerColor = Color(0xD9FFFFFF),
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = if (plantWateringFullLess(plantWater!!)[0].contains(
                                    calcSoilMoisture(soilMoisture)
                                )
                            ) "물 주기 완료" else "조금 더 물을 주세요!",
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                        ) {
                            LottieComponent("watering", Modifier)
                        }
                    }
                },
                dismissButton = {},
                confirmButton = {}
            )
        }
        // 물주기가 완료되면 0.5초 후 자동으로 다이얼로그 닫기
        LaunchedEffect(soilMoisture) {
            if (plantWateringFullLess(plantWater!!)[0].contains(calcSoilMoisture(soilMoisture))) {
                delay(500)
                showWateringDialog = false
                isWatering = false
            }
        }
    }

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
    val showAlarmStoryDialog = remember { mutableStateOf(false) }
    val storyImage = remember { mutableIntStateOf(resourceId) }
    val storyContent = remember { mutableStateOf("") }

    if(showAlarmStoryDialog.value) {
        Stories(numberOfPages = 1, showAlarmStoryDialog, onComplete = { showAlarmStoryDialog.value = false }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .zIndex(2f)
                        .width(screenWidth / 1.2f)
                        .height(screenHeight / 2.3f)
                        .background(Color(0xC5FFFFFF), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = storyContent.value,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                            fontSize = 25.sp,
                            color = Color(0xA8000000),
                            lineHeight = 40.sp
                            )
                    }
                }
                Box {
                    Image(
                        painter = painterResource(id = storyImage.intValue),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .height(screenHeight)
                            .zIndex(1f)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.height(screenHeight)
    ) {
        // 알림 스토리
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            modifier = Modifier
                .width(screenWidth)
                .wrapContentHeight()
                .heightIn(max = screenHeight / 5.3f)
                .padding(vertical = 10.dp, horizontal = 3.dp)
                .background(Color(0x75FCF8F7), RoundedCornerShape(10.dp))
        ) {
            items(1) {
                Row(
                    verticalAlignment = Alignment.Top,
                ) {
                    AlaramStoryButton(resourceId, storyImage, storyContent, soilMoistures, myPlantAlias, plantWater, showAlarmStoryDialog)
                    myPlantAliases?.let { alias ->
                        repeat(alias.size) {
                            if(!myPlantFavorites!![it]) {
                                val imageId = context.resources.getIdentifier(
                                    myPlantImages!![it] ?: "no_image",
                                    "drawable", context.packageName
                                )
                                plantViewModel.getPlantById(myPlantIds!![it])
                                // 식물 ID에 따른 soilMoisture 값을 가져오기
                                val otherSoilMoisturesLiveData = moduleViewModel.getSoilModulesByMyPlantId(
                                    myPlantIds!![it])

                                // LiveData 구독
                                val otherSoilMoistures by otherSoilMoisturesLiveData.observeAsState(emptyList())

                                AlaramStoryButton(imageId, storyImage, storyContent, otherSoilMoistures, myPlantAliases!![it], plantWater, showAlarmStoryDialog)
                            }
                        }
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight / 4)
                .padding(horizontal = 10.dp)
        ) {
            // Title
            Box(
                modifier = Modifier
                    .height(screenHeight / 4)
                    .widthIn(min = screenWidth / 1.7f, max = screenWidth / 1.6f)
                    .background(Color(0xC626A69A), RoundedCornerShape(16.dp))
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(10.dp)
                ) {
                    Text(
                        text = myPlantAlias ?: "-",
                        fontSize = if(((myPlantAlias?.length) ?: 0) > 10) 28.sp else 36.sp,
                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                        textDecoration = TextDecoration.Underline,
                        color = Color(0xFFFCF8F7)
                    )
                    Text(
                        text = "주인님과 함께 한지",
                        fontSize = 25.sp,
                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                        color = Color(0xFFFCF8F7)
                    )
                    Text(
                        text = duration?: "-일째",
                        fontSize = 35.sp,
                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                        color = Color(0xFFFCF8F7)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .fillMaxHeight()
            ) {
                // Edit Alias
                AdditionalButton(
                    Color(0xC3FFCA28),
                    Color(0xFFF4511E),
                    Icons.Outlined.Edit,
                    "별명 변경"
                    ){ showEditAliasDialog = true }
                if(showEditAliasDialog){
                    AlertDialog(
                        onDismissRequest = { showEditAliasDialog = false },
                        containerColor = Color(0xD9FFFFFF),
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                verticalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(
                                    text = "주인님의 이름은..?",
                                    textAlign = TextAlign.Center,
                                    fontSize = 30.sp,
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 20.dp)
                                )

                                BasicTextField(
                                    value = text.value,
                                    onValueChange = { text.value = it },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    ),
                                    modifier = Modifier.padding(bottom = 5.dp),
                                    decorationBox = { innerTextField ->
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .fillMaxWidth()
                                                .border(
                                                    2.dp,
                                                    Color(0xA6F57F17),
                                                    RoundedCornerShape(size = 16.dp)
                                                )
                                                .background(
                                                    Color.White,
                                                    RoundedCornerShape(size = 16.dp)
                                                )
                                                .padding(all = 15.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Spacer(modifier = Modifier.width(width = 8.dp))
                                            innerTextField()
                                        }
                                    },
                                )
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showEditAliasDialog = false },
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(Color.Transparent)
                            ){
                                Text(
                                    text = "취소",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    color = Color.Black
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showEditAliasDialog = false
                                    myPlantId.takeIf { it != 0L }?.let {
                                        myPlantUpdateViewModel.updateMyPlantAlias(text.value,
                                            it
                                        )
                                    }
                                },
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(Color.Transparent)
                            ){
                                Text(
                                    text = "변경",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    color = Color.Black
                                )
                            }
                        }
                    )
                }

                // Change MyPlant
                AdditionalButton(
                    Color(0xAB03E596),
                    Color(0xFFA95AB6),
                    Icons.Outlined.ChangeCircle,
                    "주인님 변경"
                ){ showChangeMyPlantDialog = true }
                if(showChangeMyPlantDialog){
                    AlertDialog(
                        onDismissRequest = { showChangeMyPlantDialog = false },
                        containerColor = Color(0xD9FFFFFF),
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                verticalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(
                                    text = "주인님 바꾸기",
                                    textAlign = TextAlign.Center,
                                    fontSize = 30.sp,
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                                ChangeMyPlant(myPlantIds, myPlantAliases, myPlantImages, myPlantFavorites)
                            }
                        },
                        dismissButton = {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(0.7f)
                            ) {
                                Button(
                                    onClick = {
                                        if(myPlantDeleteViewModel.myPlantId != -1L) {
                                            showChangeMyPlantDialog = false

                                            myPlantId?.let {
                                                myPlantDeleteViewModel.deleteMyPlantByMyPlantId()
                                                myPlantDeleteViewModel.resetPlantDeletedState()
                                            }

                                            if (isConnecting.value) bleManager.disconnectGatt()
                                        } else {
                                            val firstMyPlantId = myPlantIds!!.filterIndexed { index, _ ->
                                                index != myPlantId?.let { myPlantIds!!.indexOf(it) }
                                            }.firstOrNull()
                                            println(firstMyPlantId)

                                            android.app.AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
                                                .setTitle("$myPlantAlias 주인님과 이별하기")
                                                .setMessage("정말 주인님과 이별하실 건가요?")
                                                .setNegativeButton("아니요") { dialog, _ ->
                                                    dialog.dismiss()}
                                                .setPositiveButton("이별할게요") { dialog, _ ->
                                                    if (firstMyPlantId != null) {
                                                        coroutineScope.launch {
                                                            myPlantId?.let {
                                                                myPlantDeleteViewModel.myPlantId = myPlantId!!
                                                                myPlantDeleteViewModel.deleteMyPlantByMyPlantId()
                                                                myPlantDeleteViewModel.resetPlantDeletedState()
                                                                removeAlarm(context, myPlantId!!.toInt())
                                                            }

                                                            withContext(Dispatchers.IO) {
                                                                myPlantUpdateViewModel.myPlantId = firstMyPlantId
                                                                myPlantUpdateViewModel.updateMyPlantFavorite()
                                                            }

                                                            withContext(Dispatchers.Main) {
                                                                myPlantViewModel.getAllMyPlants()
                                                                myPlantViewModel.getFavoriteMyPlant()

                                                                if(isConnecting.value) {
                                                                    bleManager.disconnectGatt()
                                                                }

                                                                isModuleValueLoaded = false
                                                                dialog.dismiss()
                                                                showChangeMyPlantDialog = false
                                                            }
                                                        }
                                                    } else {
                                                        coroutineScope.launch {
                                                            myPlantId?.let {
                                                                myPlantDeleteViewModel.myPlantId = myPlantId!!
                                                                myPlantDeleteViewModel.deleteMyPlantByMyPlantId()
                                                                myPlantDeleteViewModel.resetPlantDeletedState()
                                                            }

                                                            withContext(Dispatchers.Main) {
                                                                myPlantViewModel.getAllMyPlants()
                                                                myPlantViewModel.getFavoriteMyPlant()

                                                                if(isConnecting.value) {
                                                                    bleManager.disconnectGatt()
                                                                }

                                                                isModuleValueLoaded = false
                                                                dialog.dismiss()
                                                                showChangeMyPlantDialog = false
                                                            }
                                                        }
                                                    }
                                                }
                                                .show()
                                                .apply {
                                                    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                                                    val metrics = windowManager.currentWindowMetrics
                                                    val currentWidth = metrics.bounds.width()

                                                    val layoutParams = WindowManager.LayoutParams().apply {
                                                        copyFrom(window?.attributes)
                                                        width = (currentWidth * 0.9f).toInt()
                                                        height = WindowManager.LayoutParams.WRAP_CONTENT
                                                    }
                                                    window?.attributes = layoutParams
                                                    findViewById<TextView>(android.R.id.message)?.apply {
                                                        textSize = 14f
                                                        setTextColor(ContextCompat.getColor(context, R.color.grey))
                                                        typeface = ResourcesCompat.getFont(context, R.font.spoqahansansneo_regular)
                                                    }
                                                    findViewById<TextView>(androidx.appcompat.R.id.alertTitle)?.apply {
                                                        textSize = 20f
                                                        setTextColor(ContextCompat.getColor(context, R.color.black))
                                                        typeface = ResourcesCompat.getFont(context, R.font.dohyeon_regular)
                                                    }
                                                    findViewById<Button>(android.R.id.button1)?.apply {
                                                        textSize = 14f
                                                        setTextColor(ContextCompat.getColor(context, R.color.black))
                                                        typeface = ResourcesCompat.getFont(context, R.font.spoqahansansneo_bold)
                                                    }
                                                }
                                        }
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                                ) {
                                    Text(
                                        text = "이별하기",
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                        color = Color.Red
                                    )
                                }

                                Button(
                                    onClick = { showChangeMyPlantDialog = false },
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                                ) {
                                    Text(
                                        text = "취소",
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                        color = Color.Black
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showChangeMyPlantDialog = false

                                    myPlantId?.let {
                                        // 상태 업데이트를 비동기로 처리
                                        coroutineScope.launch {
                                            myPlantUpdateViewModel.updateMyPlantFavorite()
                                            // 업데이트 완료를 기다린 후 데이터 새로고침
                                            withContext(Dispatchers.Main) {
                                                myPlantViewModel.getAllMyPlants()
                                                myPlantViewModel.getFavoriteMyPlant()
                                            }
                                        }
                                    }

                                    isModuleValueLoaded = false
                                    if(isConnecting.value) bleManager.disconnectGatt()
                                },
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(Color.Transparent)
                            ){
                                Text(
                                    text = "변경",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    color = Color.Black
                                )
                            }
                        }
                    )
                }

                // Add New MyPlant
                AdditionalButton(
                    Color(0xAB39A626),
                    Color(0xFFFFFFFF),
                    Icons.Outlined.Add,
                    "새로운 주인님"
                ){ showAddPlantDialog = true }
                if(showAddPlantDialog){
                    AlertDialog(
                        onDismissRequest = { showAddPlantDialog = false },
                        containerColor = Color(0xD9FFFFFF),
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                verticalArrangement = Arrangement.SpaceAround
                            ) {
                                Box(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.TopEnd

                                ){
                                    IconButton(
                                        modifier = Modifier.size(20.dp),
                                        onClick = { showAddPlantDialog = false }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Clear,
                                            contentDescription = null,
                                            tint = Color.Black
                                        )
                                    }
                                }
                                Text(
                                    text = "새로운 주인님은..?",
                                    textAlign = TextAlign.Center,
                                    fontSize = 33.sp,
                                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ChooseButton(name = "사진으로", painterResource(id = R.drawable.find_camera), modifier = Modifier.fillMaxWidth(0.5f)) {
                                        mainPlantNavController.navigate("camera/${Uri.encode("add")}")
                                        showAddPlantDialog = false
                                    }

                                    ChooseButton(name = "이름으로", painterResource(id = R.drawable.find_name), modifier = Modifier) {
                                        mainPlantNavController.navigate(MainPlantNavItem.AddPlantName.route)
                                        showAddPlantDialog = false
                                    }
                                }
                            }
                        },
                        confirmButton = { }
                    )
                }

                // Record MyPlant
                AdditionalButton(
                    Color(0x7E7E57C2),
                    Color(0xFFFFFFFF),
                    Icons.Outlined.CalendarToday,
                    "주인님 일지"
                ){ mainPlantNavController.navigate("add_myplant_record/${Uri.encode(myPlantId.toString())}") }

                // Check MyPlant
                AdditionalButton(
                    Color(0x7EF06292),
                    Color(0xFFFFFFFF),
                    Icons.Outlined.MedicalServices,
                    "질병 확인"
                ){ mainPlantNavController.navigate("camera/${Uri.encode("disease")}") }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Chart
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color(0x82FCF8F7), RoundedCornerShape(10.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "주인님 건강 상태",
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    color = Color(0xAE000000),
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
                if (isConnecting.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            mainPlantNavController.navigate("scan/${Uri.encode(myPlantId.toString())}")
                        }
                    ) {
                        Text(
                            text = "주인님과 연결됨",
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                            color = Color(0xAE03834B),
                            modifier = Modifier
                                .background(Color(0xAEC7D1C7), RoundedCornerShape(8.dp))
                                .padding(3.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        IconButton(
                            modifier = Modifier
                                .background(Color(0xAEC7D1C7), RoundedCornerShape(8.dp))
                                .padding(1.dp)
                                .size(20.dp),
                            onClick = {
                                mainPlantNavController.navigate("scan_connect/${Uri.encode(myPlantId.toString())}")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Bluetooth,
                                contentDescription = null,
                                tint = Color(0xAE03834B)
                            )
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                mainPlantNavController.navigate("scan_connect/${Uri.encode(myPlantId.toString())}")
                            }
                    ) {
                        Text(
                            text = "주인님과 연결끊김",
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                            color = Color(0xAEDF0606),
                            modifier = Modifier
                                .background(Color(0xAEC7D1C7), RoundedCornerShape(8.dp))
                                .padding(3.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        IconButton(
                            modifier = Modifier
                                .background(Color(0xAEC7D1C7), RoundedCornerShape(8.dp))
                                .padding(1.dp)
                                .size(20.dp),
                            onClick = {
                                mainPlantNavController.navigate("scan_connect/${Uri.encode(myPlantId.toString())}")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PowerSettingsNew,
                                contentDescription = null,
                                tint = Color(0xAEDF0606)
                            )
                        }
                    }
                }
            }

            if(moduleValues.isEmpty()) {
                Column(
                    modifier = Modifier
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(top = 4.dp, bottom = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(screenHeight / 4.1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 4.dp)
                                    .haze(hazeState)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier
                                        .padding(top = 4.dp, bottom = 4.dp)
                                        .fillMaxWidth()
                                ) {
                                    // Soil Moisture
                                    Chart(
                                        emptyList(),
                                        "",
                                        "soilMoisture",
                                        plantId,
                                        Color(0xAB3F51B5)
                                    )
                                    // Humidity
                                    Chart(
                                        emptyList(),
                                        "",
                                        "humidity",
                                        plantId,
                                        Color(0xAB4CAF50)
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier
                                        .padding(top = 4.dp, bottom = 4.dp)
                                        .fillMaxWidth()
                                ) {
                                    // Temperature
                                    Chart(
                                        emptyList(),
                                        "",
                                        "temperature",
                                        plantId,
                                        Color(0xABF44336)
                                    )
                                    // Light
                                    Chart(
                                        emptyList(),
                                        "",
                                        "light",
                                        plantId,
                                        Color(0xABFF9800)
                                    )
                                }
                            }
                            if(Build.VERSION.SDK_INT <= 30) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .drawBehind {
                                            // Paint 객체를 사용하여 블러 효과 설정
                                            val paint = Paint()
                                                .asFrameworkPaint()
                                                .apply {
                                                    isAntiAlias = true
                                                    color =
                                                        android.graphics.Color.parseColor("#EDFFFFFF") // 반투명 흰색 배경
                                                    maskFilter = BlurMaskFilter(
                                                        25f,
                                                        BlurMaskFilter.Blur.NORMAL
                                                    ) // 블러 강도
                                                }
                                            // Canvas에 블러 처리된 배경 그리기
                                            drawContext.canvas.nativeCanvas.drawRect(
                                                0f,
                                                0f,
                                                size.width,
                                                size.height,
                                                paint
                                            )
                                        }
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .hazeChild(
                                            hazeState,
                                            RoundedCornerShape(10.dp),
                                            HazeStyle(
                                                tint = Color.White.copy(.2f),
                                                blurRadius = 10.dp
                                            )
                                        )
                                )
                            }
                            if(isConnecting.value && !isModuleValueLoaded) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    Text(
                                        text = "건강 상태를 수집하고 있어요",
                                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                        color = Color.Black,
                                        fontSize = 20.sp
                                    )
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .padding(vertical = 15.dp)
                                            .size(45.dp),
                                        color = Color(0xFF009688)
                                    )
                                }
                            } else {
                                CommonButton(name = "건강 확인하기", modifier = Modifier) {
                                    mainPlantNavController.navigate("scan_connect/${Uri.encode(myPlantId.toString())}")
                                }
                            }
                        }
                    }
                }
            } else {
                if(isConnecting.value && !isModuleValueLoaded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(top = 4.dp, bottom = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight / 4.1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 4.dp)
                                    .haze(hazeStateConnected)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .padding(top = 8.dp, bottom = 4.dp)
                                        .fillMaxWidth()
                                ) {
                                    // Soil Moisture
                                    Chart(
                                        moduleValues,
                                        "토양수분\n${calcSoilMoisture(moduleValues.last().soilMoisture)}",
                                        "soilMoisture",
                                        plantId,
                                        Color(0xAB3F51B5)
                                    )

                                    myPlantId?.let { alarmViewModel.loadAlarms(it) }

                                    if(moduleValues.isNotEmpty()) {
                                        if (plantWateringFullLess(plantWater!!)[1].contains(calcSoilMoisture(moduleValues.last().soilMoisture))) {
                                            myPlantAlarmColor = Color(0xFF0C68F1)
                                            if (moduleValues.last().myPlantId == myPlantId) {
                                                AddAlarm(myPlantId, myPlantAlias)
                                            }
                                        } else if (plantWateringFullLess(plantWater!!)[0].contains(calcSoilMoisture(moduleValues.last().soilMoisture))) {
                                            myPlantAlarmColor = Color(0xFFDDDDF1)

                                            if (alarms.isNotEmpty() && !alarms.last().isDone
                                            ) {
                                                recordInputViewModel.myPlantId.value = myPlantId
                                                recordInputViewModel.title.value = "$myPlantAlias 주인님 식사 완료"
                                                recordInputViewModel.content.value =
                                                    "$myPlantAlias 주인님이 식사를 하셨어요"
                                                recordInputViewModel.date.value = Date()
                                                recordInputViewModel.image.value = "android.resource://com.android.greenmate/drawable/complete_watering"

                                                recordInputViewModel.insertRecord()

                                                alarmViewModel.updateAlarmDone(true, myPlantId!!)
                                            }

                                            DeleteAlarm(myPlantId)
                                        } else {
                                            myPlantAlarmColor = Color(0xFFF19D0C)
                                        }
                                    }
                                    // Humidity
                                    Chart(
                                        moduleValues,
                                        "습도: ${decimalFormat.format(moduleValues.last().humidity)}%\n${
                                            plantHumidity.takeIf { it != "" }?.let {
                                                calcHumidity(
                                                    it,
                                                    moduleValues.last().humidity
                                                )
                                            }
                                        }",
                                        "humidity",
                                        plantId,
                                        Color(0xAB4CAF50)
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .padding(top = 4.dp, bottom = 8.dp)
                                        .fillMaxWidth()
                                ) {
                                    // Temperature
                                    Chart(
                                        moduleValues,
                                        "온도: ${decimalFormat.format(moduleValues.last().temperature)}°C\n${
                                            plantTemperature.takeIf { it != "" }?.let {
                                                calcTemperature(
                                                    it,
                                                    moduleValues.last().temperature
                                                )
                                            }
                                        }",
                                        "temperature",
                                        plantId,
                                        Color(0xABF44336)
                                    )
                                    // Light
                                    Chart(
                                        moduleValues,
                                        "빛\n${
                                            plantLight.takeIf { it != "" }?.let {
                                                calcLight(
                                                    it,
                                                    moduleValues.last().lightIntensity
                                                )
                                            }
                                        }",
                                        "light",
                                        plantId,
                                        Color(0xABFF9800)
                                    )
                                }
                            }
                            if(Build.VERSION.SDK_INT <= 30) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .drawBehind {
                                            // Paint 객체를 사용하여 블러 효과 설정
                                            val paint = Paint()
                                                .asFrameworkPaint()
                                                .apply {
                                                    isAntiAlias = true
                                                    color =
                                                        android.graphics.Color.parseColor("#EDFFFFFF") // 반투명 흰색 배경
                                                    maskFilter = BlurMaskFilter(
                                                        25f,
                                                        BlurMaskFilter.Blur.NORMAL
                                                    ) // 블러 강도
                                                }
                                            // Canvas에 블러 처리된 배경 그리기
                                            drawContext.canvas.nativeCanvas.drawRect(
                                                0f,
                                                0f,
                                                size.width,
                                                size.height,
                                                paint
                                            )
                                        }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        Text(
                                            text = "건강 상태를 수집하고 있어요",
                                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                            color = Color.Black,
                                            fontSize = 20.sp
                                        )
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .padding(vertical = 15.dp)
                                                .size(45.dp),
                                            color = Color(0xFF009688)
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .hazeChild(
                                            hazeState,
                                            RoundedCornerShape(10.dp),
                                            HazeStyle(
                                                tint = Color.White.copy(.2f),
                                                blurRadius = 10.dp
                                            )
                                        )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        Text(
                                            text = "건강 상태를 수집하고 있어요",
                                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                                            color = Color.Black,
                                            fontSize = 20.sp
                                        )
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .padding(vertical = 15.dp)
                                                .size(45.dp),
                                            color = Color(0xFF009688)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp, bottom = 4.dp)
                                .fillMaxWidth()
                        ) {
                            // Soil Moisture
                            Chart(
                                moduleValues,
                                "토양수분\n${calcSoilMoisture(moduleValues.last().soilMoisture)}",
                                "soilMoisture",
                                plantId,
                                Color(0xAB3F51B5)
                            )

                            myPlantId.takeIf { it != 0L }?.let { alarmViewModel.loadAlarms(it) }

                            if(moduleValues.isNotEmpty()) {
                                if (plantWateringFullLess(plantWater!!)[1].contains(calcSoilMoisture(moduleValues.last().soilMoisture))) {
                                    myPlantAlarmColor = Color(0xFF0C68F1)
                                    if (moduleValues.last().myPlantId == myPlantId) {
                                        AddAlarm(myPlantId, myPlantAlias)
                                    }
                                } else if (plantWateringFullLess(plantWater!!)[0].contains(calcSoilMoisture(moduleValues.last().soilMoisture))) {
                                    myPlantAlarmColor = Color(0xFFDDDDF1)

                                    if (alarms.isNotEmpty() && !alarms.last().isDone) {
                                        recordInputViewModel.myPlantId.value = myPlantId
                                        recordInputViewModel.title.value = "$myPlantAlias 주인님 식사 완료"
                                        recordInputViewModel.content.value =
                                            "$myPlantAlias 주인님이 식사를 하셨어요"
                                        recordInputViewModel.date.value = Date()
                                        recordInputViewModel.image.value = "android.resource://com.android.greenmate/drawable/complete_watering"

                                        recordInputViewModel.insertRecord()

                                        alarmViewModel.updateAlarmDone(true, myPlantId!!)
                                    }

                                    DeleteAlarm(myPlantId)
                                } else {
                                    myPlantAlarmColor = Color(0xFFF19D0C)
                                }
                            }
                            // Humidity
                            Chart(
                                moduleValues,
                                "습도: ${decimalFormat.format(moduleValues.last().humidity)}%\n${
                                    plantHumidity.takeIf { it != "" }?.let {
                                        calcHumidity(
                                            it,
                                            moduleValues.last().humidity
                                        )
                                    }
                                }",
                                "humidity",
                                plantId,
                                Color(0xAB4CAF50)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 4.dp, bottom = 8.dp)
                                .fillMaxWidth()
                        ) {
                            // Temperature
                            Chart(
                                moduleValues,
                                "온도: ${decimalFormat.format(moduleValues.last().temperature)}°C\n${
                                    plantTemperature.takeIf { it != "" }?.let {
                                        calcTemperature(
                                            it,
                                            moduleValues.last().temperature
                                        )
                                    }
                                }",
                                "temperature",
                                plantId,
                                Color(0xABF44336)
                            )
                            // Light
                            Chart(
                                moduleValues,
                                "빛\n${
                                    plantLight.takeIf { it != "" }?.let {
                                        calcLight(
                                            it,
                                            moduleValues.last().lightIntensity
                                        )
                                    }
                                }",
                                "light",
                                plantId,
                                Color(0xABFF9800)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlaramStoryButton(
    resourceId: Int,
    storyImage: MutableIntState,
    storyContent: MutableState<String>,
    soilMoistures: List<Float>,
    myPlantAlias: String?,
    plantWater: String?,
    showAlarmStoryDialog: MutableState<Boolean>
) {
    val context = LocalContext.current
    var alarmStoryColor by remember { mutableStateOf(Color(0xFFDDDDF1))}

    val myPlantPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(resourceId)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .build(),
        contentScale = ContentScale.Crop
    )
    alarmStoryColor = if(soilMoistures.isNotEmpty()) {
        if (plantWateringFullLess(plantWater!!)[1].contains(calcSoilMoisture(soilMoistures.last()))) {
            Color(0xFF0C68F1)
        } else if(plantWateringFullLess(plantWater)[0].contains(calcSoilMoisture(soilMoistures.last()))) {
            Color(0xFFDDDDF1)
        } else {
            Color(0xFFF19D0C)
        }
    }  else {
        Color(0xFFDDDDF1)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(90.dp)
    ) {
        Button(
            onClick = {
                showAlarmStoryDialog.value = true
                storyImage.intValue = resourceId
                if(soilMoistures.isNotEmpty()) {
                    if (plantWateringFullLess(plantWater!!)[1].contains(calcSoilMoisture(soilMoistures.last()))) {
                        storyContent.value = "$myPlantAlias 주인님의 불만사항이 접수되었어요! \n주인님의 식사를 준비해주세요!"
                    } else if (plantWateringFullLess(plantWater)[0].contains(calcSoilMoisture(soilMoistures.last()))) {
                        storyContent.value = "$myPlantAlias 주인님은 \n아주 만족한 식사를 해서 \n기분이 매우 좋은 상태입니다!"
                    } else {
                        storyContent.value = "$myPlantAlias 주인님이 \n아직은 배가 부르지만\n" +
                                "언제 배가 고파질지 알 수 없는 \n" +
                                "경계의 상태에 있습니다.\n" +
                                "주의를 기울여 주세요!"
                    }
                } else {
                    storyContent.value = "$myPlantAlias 주인님의 \n건강 상태를 확인하려면 \n모듈을 통해 주인님과 연결해보세요!"
                }
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .size(80.dp)
                .padding(5.dp)
                .border(2.5.dp, alarmStoryColor, CircleShape)
        ) {
            Image(
                painter = myPlantPainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
            )
        }
        myPlantAlias?.let {
            Text(
                text = it,
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                textAlign = TextAlign.Center,
                lineHeight = 10.sp,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            )
        }
    }
}


@Composable
fun Stories(
    numberOfPages: Int,
    showAlarmStoryDialog: MutableState<Boolean>,
    indicatorModifier: Modifier = Modifier
        .padding(top = 12.dp, bottom = 12.dp)
        .clip(RoundedCornerShape(12.dp)),
    spaceBetweenIndicator: Dp = 4.dp,
    indicatorBackgroundColor: Color = Color.LightGray,
    indicatorProgressColor: Color = Color.White,
    indicatorBackgroundGradientColors: List<Color> = emptyList(),
    slideDurationInSeconds: Long = 5,
    touchToPause: Boolean = true,
    hideIndicators: Boolean = false,
    onEveryStoryChange: ((Int) -> Unit)? = null,
    onComplete: () -> Unit,
    content: @Composable (Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { numberOfPages })
    val coroutineScope = rememberCoroutineScope()

    var pauseTimer by remember {
        mutableStateOf(false)
    }

    val deltaY = remember { mutableFloatStateOf(0f) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier
        .zIndex(2f)
        .fillMaxSize()
        .offset { IntOffset(0, deltaY.floatValue.toInt()) }
    ) {
        //Full screen content behind the indicator
        StoryImage(
            pagerState = pagerState,
            showAlarmStoryDialog,
            deltaY,
            onTap = {
            if (touchToPause)
                pauseTimer = it
        }, content)

        //Indicator based on the number of items
        val modifier =
            if (hideIndicators) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            if (indicatorBackgroundGradientColors.isEmpty()) listOf(
                                Color.Black,
                                Color.Transparent
                            ) else indicatorBackgroundGradientColors
                        )
                    )
            }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.padding(spaceBetweenIndicator))

            ListOfIndicators(
                numberOfPages,
                indicatorModifier,
                indicatorBackgroundColor,
                indicatorProgressColor,
                slideDurationInSeconds,
                pauseTimer,
                hideIndicators,
                coroutineScope,
                pagerState,
                spaceBetweenIndicator,
                onEveryStoryChange = onEveryStoryChange,
                onComplete = onComplete,
            )
        }
        Box(
            modifier = Modifier
                .size(screenWidth)
                .zIndex(2f)
                .padding(0.dp, 30.dp, 10.dp, 0.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(
                onClick = { showAlarmStoryDialog.value = false }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.ListOfIndicators(
    numberOfPages: Int,
    indicatorModifier: Modifier,
    indicatorBackgroundColor: Color,
    indicatorProgressColor: Color,
    slideDurationInSeconds: Long,
    pauseTimer: Boolean,
    hideIndicators: Boolean,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    spaceBetweenIndicator: Dp,
    onEveryStoryChange: ((Int) -> Unit)? = null,
    onComplete: () -> Unit,
) {
    var currentPage by remember {
        mutableStateOf(0)
    }

    for (index in 0 until numberOfPages) {
        LinearIndicator(
            modifier = indicatorModifier.weight(1f),
            index == currentPage,
            indicatorBackgroundColor,
            indicatorProgressColor,
            slideDurationInSeconds,
            pauseTimer,
            hideIndicators
        ) {
            coroutineScope.launch {

                currentPage++

                if (currentPage < numberOfPages) {
                    onEveryStoryChange?.invoke(currentPage)
                    pagerState.animateScrollToPage(currentPage)
                }

                if (currentPage == numberOfPages) {
                    onComplete()
                }
            }
        }

        Spacer(modifier = Modifier.padding(spaceBetweenIndicator))
    }
}

@Composable
fun AdditionalButton(
    backgroundColor: Color,
    tintColor: Color,
    icon: ImageVector,
    title: String,
    onClicked: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier
            .height(screenHeight / 20)
            .padding(bottom = 5.dp)
            .background(backgroundColor, RoundedCornerShape(10.dp))
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            contentPadding = PaddingValues(0.dp),
            onClick = { onClicked() }
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier
                        .size(18.dp)
                )
                Text(
                    modifier = Modifier
                        .padding(start = 5.dp),
                    text = title,
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ChangeMyPlant(
    myPlantIds: List<Long>?,
    myPlantAliases: List<String>?,
    myPlantImages: List<String>?,
    myPlantFavorites: List<Boolean>?,
    myPlantUpdateViewModel: MyPlantUpdateViewModel = hiltViewModel(),
    myPlantDeleteViewModel: MyPlantDeleteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val favoriteIndex = myPlantFavorites?.indexOf(true)
    var selectedIndex by remember { mutableIntStateOf(favoriteIndex ?: 0) } // 선택된 인덱스를 관리하는 변수

    Column(
        modifier = Modifier
            .fillMaxHeight(0.7f)
            .padding(0.dp, 5.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 가로 2개씩 배치
            verticalArrangement = Arrangement.spacedBy(10.dp), // 세로 간격
            horizontalArrangement = Arrangement.spacedBy(15.dp), // 가로 간격
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp)
        ) {
            myPlantAliases?.let {
                items(it.size) { index ->
                    val imageId = context.resources.getIdentifier(
                        myPlantImages?.get(index) ?: "no_image",
                        "drawable", context.packageName
                    )

                    if(index == selectedIndex) {
                        myPlantUpdateViewModel.myPlantId = myPlantIds!![index]
                    }

                    if(favoriteIndex == selectedIndex) {
                        myPlantDeleteViewModel.myPlantId = -1L
                    }

                    Column {
                        Button(
                            onClick = {
                                selectedIndex =
                                    if (selectedIndex == index) selectedIndex else index // 클릭한 버튼이 이미 선택된 경우 해제
                                myPlantUpdateViewModel.myPlantId = myPlantIds!![selectedIndex]
                                if(selectedIndex != favoriteIndex) {
                                    myPlantDeleteViewModel.myPlantId = myPlantIds[selectedIndex]
                                } else {
                                    myPlantDeleteViewModel.myPlantId = -1L
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(150.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = imageId),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(10.dp))
                                )
                                if (selectedIndex == index) { // 선택된 버튼만 체크 표시
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .background(
                                                Color(0x58000000),
                                                RoundedCornerShape(10.dp)
                                            )
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.checked),
                                            contentDescription = "checked"
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = it[index],
                            fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                            lineHeight = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Chart(
    moduleValues: List<Module>,
    title: String,
    moduleValue: String,
    plantId: Long?,
    color: Color
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(16.dp))
            .height(screenHeight / 9)
            .width(screenWidth / 2.5f)
    ) {
        if (moduleValues.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                color = Color.White,
                modifier = Modifier
                    .padding(10.dp)
            )
            if (plantId != null) {
                ModulePreviewChart(moduleValues, moduleValue, plantId)
            }
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "데이터가 없습니다",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun EveryDayAlarm(
    myPlantId: Long?,
    myPlantAlias: String?,
    alarmViewModel: AlarmViewModel = hiltViewModel()
) {
    val reminderId = myPlantId?.toInt()
    val title = "[알림] $myPlantAlias 주인님이 당신을 기다립니다!!"
    val message = "$myPlantAlias 주인님의 건강상태 확인을 위해 접속해주세요 😭"
    val context = LocalContext.current

    val alarmEntity = myPlantId?.let {
        AlarmEntity(
            myPlantId = it,
            title = title,
            isDone = false
        )
    }
    if (alarmEntity != null) {
        alarmViewModel.saveOrUpdateAlarm(alarmEntity)
    }

    if (reminderId != null) {
        scheduledAlarm(
            context = context,
            reminderId = reminderId, // Unique ID for this reminder
            title = title,
            message = message,
            day = 1
        )
    }
}

@Composable
fun AddAlarm(
    myPlantId: Long?,
    myPlantAlias: String?,
    alarmViewModel: AlarmViewModel = hiltViewModel()
) {
    val reminderId = myPlantId?.toInt()
    val title = "[알림] $myPlantAlias 주인님의 불만사항이 접수됐어요!!"
    val message = "$myPlantAlias 주인님이 목 말라요 😭"
    val context = LocalContext.current

    val alarmEntity = myPlantId?.let {
        AlarmEntity(
            myPlantId = it,
            title = title,
            isDone = false
        )
    }
    if (alarmEntity != null) {
        alarmViewModel.saveOrUpdateAlarm(alarmEntity)
    }

    if (reminderId != null) {
        scheduledAlarm(
            context = context,
            reminderId = reminderId, // Unique ID for this reminder
            title = title,
            message = message,
            day = 1
        )
    }
}

@Composable
fun DeleteAlarm(
    myPlantId: Long?
){
    val reminderId = myPlantId?.toInt()
    val context = LocalContext.current

    if (reminderId != null) {
        removeAlarm(
            context = context,
            reminderId = reminderId // 삭제할 알림의 ID
        )
    }
}

fun plantWateringFullLess(
    plantWater: String
): List<List<String>> {
     return when(plantWater) {
         "평균 주 2회 이상" -> listOf(listOf("아주 촉촉해"), listOf("촉촉해","적당해","건조해","너무 건조해"))
         "평균 주 1~2회" -> listOf(listOf("아주 촉촉해"), listOf("적당해","건조해","너무 건조해"))
         "평균 월 1~2회" -> listOf(listOf("아주 촉촉해"), listOf("건조해","너무 건조해"))
         "평균 월 1회" -> listOf(listOf("아주 촉촉해","촉촉해"), listOf("너무 건조해"))
         "평균 월 1회 이하" -> listOf(listOf("아주 촉촉해","촉촉해","적당해"), listOf("너무 건조해"))
         else -> listOf(listOf("오류"), listOf("오류"))
     }
}

fun calcSoilMoisture(
    soilMoisture: Float
): String {
    val airValue = 951  //951 <<< 긴 토양모듈 / 짧은 토양모듈 >>> 765
    val waterValue = 0  //0 <<< 긴 토양모듈 / 짧은 토양모듈 >>> 290

    val intervals = (airValue - waterValue) / 5

    return when {
        soilMoisture <= waterValue -> {
            "오류"
        }
        soilMoisture < (waterValue + intervals) -> {
            "아주 촉촉해"
        }
        soilMoisture < (waterValue + 2 * intervals) -> {
            "촉촉해"
        }
        soilMoisture < (waterValue + 3 * intervals) -> {
            "적당해"
        }
        soilMoisture < (waterValue + 4 * intervals) -> {
            "건조해"
        }
        else -> {
            "너무 건조해"
        }
    }
}

fun calcTemperature(
    plantTemperature: String,
    temperature: Float
): String {
    val minTemperature = plantTemperature.trim().substring(0,2).toFloat()
    val maxTemperature = plantTemperature.trim().substring(3,5).toFloat()

    return if(temperature in minTemperature..maxTemperature) {
        "적당해"
    } else if(minTemperature > temperature) {
        "추워"
    } else {
        "더워"
    }
}

fun calcHumidity(
    plantHumidity: String,
    humidity: Float
): String {
    val isRange = plantHumidity.contains("~")
    val isMoreOrLess = if(!isRange && plantHumidity.contains("이상")) "more" else "less"

    val minHumidity = if(isRange) plantHumidity.trim().substring(0,2).toFloat() else -1f
    val maxHumidity = if(isRange) plantHumidity.trim().substring(3,5).toFloat() else -1f
    val boundaryValue = if(!isRange) plantHumidity.trim().substring(0,2).toFloat() else -1f

    return if(isRange) {
        if(humidity in minHumidity..maxHumidity) {
            "적당해"
        } else if(minHumidity > humidity) {
            "건조해"
        } else {
            "습해"
        }
    } else {
        if(isMoreOrLess == "more") {
            if(boundaryValue <= humidity) {
                "적당해"
            } else {
                "건조해"
            }
        } else {
            if(humidity <= boundaryValue) {
                "적당해"
            } else {
                "습해"
            }
        }
    }
}

fun calcLight(
    plantLight: String,
    light: Float
): String {
    val sunnySpot = 30000f
    val halfSunnySpot = 5000f
    val halfDarkSpot = 2000f
    val darkSpot = 300f

    return when(plantLight) {
        "양지" -> {
            if(sunnySpot <= light) {
                "노곤노곤해"
            } else {
                "햇빛이 필요해"
            }
        }
        "반양지" -> {
            if(light in halfSunnySpot..sunnySpot) {
                "노곤노곤해"
            } else if(sunnySpot < light) {
                "그늘로 옮겨줘"
            } else {
                "햇빛이 필요해"
            }
        }
        "반음지" -> {
            if(light in halfDarkSpot..halfSunnySpot) {
                "노곤노곤해"
            } else if(halfSunnySpot < light) {
                "그늘로 옮겨줘"
            } else {
                "햇빛이 필요해"
            }
        }
        else -> {
            if(darkSpot <= light) {
                "노곤노곤해"
            } else {
                "햇빛이 필요해"
            }
        }
    }
}

fun getDayDifferenceText(storedDate: Date): String {
    // 1. 현재 시간을 가져오기
    val currentDate = Date()

    // 2. 현재 시간과 DB에서 가져온 Date의 차이 계산 (밀리초 단위)
    val diffInMillis = currentDate.time - storedDate.time

    // 3. 차이를 "일" 단위로 변환
    val daysDifference = TimeUnit.MILLISECONDS.toDays(diffInMillis)

    // 4. "n일째" 형식으로 변환하여 반환
    return "${daysDifference + 1}일째" // 0일차는 1일째로 표시하려면 +1
}

// SharedPreferences에서 BLE 리스트 불러오는 함수
private fun loadBle(sharedPreferences: SharedPreferences, myPlantId: String?): DeviceData {
    val key = "ble_$myPlantId" // myPlantId를 포함한 고유 키 생성
    val deviceDataList = sharedPreferences.getStringSet(key, emptySet())?.toList() ?: emptyList()
    val deviceData = if (deviceDataList.isNotEmpty()) DeviceData(deviceDataList[0], deviceDataList[2], deviceDataList[1]) else DeviceData("","","")
    return deviceData
}
