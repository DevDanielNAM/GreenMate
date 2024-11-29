package com.android.greenmate.presentation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.android.greenmate.domain.model.DeviceData
import com.android.greenmate.R
import com.android.greenmate.data.ble.BleManager
import com.android.greenmate.domain.BleInterface
import com.android.greenmate.presentation.navigation.MainPlantNavItem
import com.android.greenmate.presentation.ui.components.CheckMultiplePermissions
import com.android.greenmate.presentation.ui.components.HorizontalDivider
import com.android.greenmate.presentation.ui.components.LottieComponent
import com.android.greenmate.presentation.viewmodel.ModuleInputViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BleScanConnectScreen(
    mainPlantNavController: NavHostController,
    bleManager: BleManager,
    myPlantId: String?,
    moduleInputViewModel: ModuleInputViewModel = hiltViewModel()
) {
    val scanList = remember { mutableStateListOf<DeviceData>() }
    val isScanning = remember { mutableStateOf(false) }
    val isConnecting = remember { mutableStateOf(bleManager.isDeviceConnected()) }
    val context = LocalContext.current

    // 연결된 기기 정보를 관리하는 상태
    val connectedDeviceData = remember { mutableStateOf<DeviceData?>(bleManager.getConnectedDeviceData()) }

    var light by remember { mutableFloatStateOf(0.0f) }
    var temperature by remember { mutableFloatStateOf(0.0f) }
    var humidity by remember { mutableFloatStateOf(0.0f) }
    var soilMoisture by remember { mutableFloatStateOf(0.0f) }

    // BleScanScreen에 진입할 때마다 연결된 기기 정보 확인
    LaunchedEffect(Unit) {
        connectedDeviceData.value = bleManager.getConnectedDeviceData()
    }

    LaunchedEffect(Unit) {
        var lastSavedTime = System.currentTimeMillis()

        withContext(Dispatchers.IO) {
            bleManager.onConnectedStateObserve(object : BleInterface {
                override fun onConnectedStateObserve(isConnected: Boolean, data: String) {
                    Log.d("BLE", "isConnected: $isConnected, data: $data")
                    isConnecting.value = isConnected
                    connectedDeviceData.value = if (isConnected) {
                        bleManager.getConnectedDeviceData()
                    } else {
                        null
                    }
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
                        moduleInputViewModel.myPlantId = myPlantId.toLong()
                    }
                    // 모듈 연결 시 딜레이로 인한 0 값 제외
                    if (light != 0f && temperature != 0f && humidity != 0f && soilMoisture != 0f) {
                        val currentTime = System.currentTimeMillis()

                        // 10초마다 저장
                        if (currentTime - lastSavedTime >= 10 * 1000) {
                            Log.d("Module_insert", "insert")
                            moduleInputViewModel.insertModule()
                            lastSavedTime = currentTime // 마지막 저장 시간 업데이트
                        }
                    }
                }
            })
        }
    }

    LaunchedEffect(isConnecting.value) {
        isConnecting.value = bleManager.isDeviceConnected()
    }

    // 주기적으로 연결 상태를 확인합니다.
    LaunchedEffect(connectedDeviceData.value) {
        connectedDeviceData.value = bleManager.getConnectedDeviceData()
        Log.d("BleScanScreen", "Connected device data: ${connectedDeviceData.value}")
    }

    val permissionsList = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_SCAN
            Manifest.permission.BLUETOOTH_ADVERTISE
            Manifest.permission.BLUETOOTH_CONNECT
        } else {
            Manifest.permission.BLUETOOTH
            Manifest.permission.BLUETOOTH_ADMIN
            Manifest.permission.ACCESS_COARSE_LOCATION
            Manifest.permission.ACCESS_FINE_LOCATION
        }
    )
    val permissionState = rememberMultiplePermissionsState(permissions = permissionsList)
    val showPermissionDialog = remember { mutableStateOf(false) }

    bleManager.setScanList(scanList)
    Log.d("BleScanConnecting", "${bleManager.isDeviceConnected()}")


    HandlePermissionActions(permissionState = permissionState, showPermissionDialog = showPermissionDialog)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
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
    )
    {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier.padding(16.dp, 0.dp),
                text = "연결할 모듈을 선택하세요",
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
            )
            if (isConnecting.value) {
                Text(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    text = "연결된 기기: ${connectedDeviceData.value?.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
                Box(
                    contentAlignment = Alignment.TopStart,
                    modifier = Modifier
                        .height(100.dp)
                        .padding(horizontal = 20.dp),
                ) {
                    ScanItem(mainPlantNavController, bleManager, connectedDeviceData.value!!, myPlantId, isScanning, isConnecting)
                }
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Text(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    text = "연결된 기기가 없습니다.",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                    color = Color.Red
                )
                ScanList(mainPlantNavController, bleManager, scanList, myPlantId, isScanning, isConnecting)
                ScanButton(context, bleManager, isScanning, permissionState, showPermissionDialog)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@SuppressLint("MissingPermission")
fun ScanButton(
    context: Context,
    bleManager: BleManager,
    isScanning: MutableState<Boolean>,
    permissionState: MultiplePermissionsState,
    showPermissionDialog: MutableState<Boolean>
) {
    LaunchedEffect(isScanning.value) {
        if(!permissionState.allPermissionsGranted) {
            isScanning.value = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            modifier = Modifier
                .padding(10.dp)
                .height(50.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5FA777)),
            shape = RoundedCornerShape(10.dp),
            onClick = {
                if (!isScanning.value) {
                    if(permissionState.allPermissionsGranted) {
                        bleManager.startBleScan()
                        isScanning.value = !isScanning.value
                    } else {
                        showPermissionDialog.value = true
                    }
                } else {
                    bleManager.stopBleScan()
                    isScanning.value = !isScanning.value
                }
            }
        ) {
            Text(
                text = if (!isScanning.value) {
                    "찾기"
                } else {
                    "멈추기"
                },
                fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun ScanList(
    navController: NavHostController,
    bleManager: BleManager,
    scanList: SnapshotStateList<DeviceData>,
    myPlantId: String?,
    isScanning: MutableState<Boolean>,
    isConnecting: MutableState<Boolean>
) {
    if(scanList.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            items(scanList) { topic ->
                ScanItem(navController, bleManager, topic, myPlantId, isScanning, isConnecting)
                HorizontalDivider()
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            LottieComponent(fileName = "finding_module", Modifier)
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun ScanItem(
    mainNavController: NavHostController,
    bleManager: BleManager,
    deviceData: DeviceData,
    myPlantId: String?,
    isScanning: MutableState<Boolean>,
    isConnecting: MutableState<Boolean>
) {
    val showConnectDialog = remember { mutableStateOf(false) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.padding(vertical = 5.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = rememberRipple(color = Color.Green),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    bleManager.stopBleScan()
                    isScanning.value = false
                    showConnectDialog.value = true
                }
                .height(60.dp)
                .padding(start = 5.dp)
        ) {
            Text(
                text = deviceData.name,
                fontSize = 20.sp
            )
        }
    }
    if(showConnectDialog.value) {
        AlertDialog(
            onDismissRequest = { showConnectDialog.value = false },
            containerColor = Color(0xFFFFFFFF),
            text = {
                Column {
                    Text(
                        text = deviceData.name,
                        fontSize = 30.sp,
                        fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                        textDecoration = TextDecoration.Underline,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 35.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    )

                    Text(
                        text = "${deviceData.name} 모듈을 연결하여 주인님의 건강 상태를 확인하시겠습니까?",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.spoqahansansneo_regular)),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    )
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    onClick = {
                        bleManager.startBleScan()
                        isScanning.value = true
                        showConnectDialog.value = false
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "취소",
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                    )
                }
            },
            confirmButton = {
                ConnectButton(mainNavController, bleManager, deviceData, myPlantId, showConnectDialog, isScanning, isConnecting)
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun HandlePermissionActions(
    permissionState: MultiplePermissionsState,
    showPermissionDialog: MutableState<Boolean>
) {
    if (showPermissionDialog.value) {
        CheckMultiplePermissions(
            permissionState = permissionState,
            onPermissionResult = { if (it) showPermissionDialog.value = false },
            showPermissionDialog = showPermissionDialog
        )
    }
}

@SuppressLint("MissingPermission")
@Composable
fun ConnectButton(
    mainNavController: NavHostController,
    bleManager: BleManager,
    deviceData: DeviceData?,
    myPlantId: String?,
    showConnectDialog: MutableState<Boolean>,
    isScanning: MutableState<Boolean>,
    isConnecting: MutableState<Boolean>
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("ble_prefs", Context.MODE_PRIVATE)

    if(!isConnecting.value) {
        Button(
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            enabled = !isConnecting.value,
            contentPadding = PaddingValues(0.dp),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    if (deviceData != null) {
                        if (myPlantId != null) {
                            bleManager.startBleConnectGatt(deviceData ?: DeviceData("", "", ""))

                            saveBle(sharedPreferences, deviceData, myPlantId)
                        }
                    }
                    showConnectDialog.value = false
                    isScanning.value = false
                    delay(500)
                }
                mainNavController.navigate(MainPlantNavItem.MyPlantMain.route)
            }
        ) {
            Text(
                text = "연결",
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
            )
        }
    } else {
        Button(
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            enabled = isConnecting.value,
            contentPadding = PaddingValues(0.dp),
            onClick = {
                bleManager.disconnectGatt()
                if (myPlantId != null) {
                    removeBleFromPreferences(context, myPlantId)
                }
                showConnectDialog.value = false
                isScanning.value = false
            }
        ) {
            Text(
                text = "연결해제",
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
            )
        }
    }
}

// SharedPreferences에 BLE 리스트 저장하는 함수
private fun saveBle(sharedPreferences: SharedPreferences, deviceData: DeviceData, myPlantId: String) {
    val key = "ble_$myPlantId" // myPlantId를 포함한 고유 키 생성
    val deviceDataList = listOf(deviceData.name, deviceData.uuid, deviceData.address)
    with(sharedPreferences.edit()) {
        putStringSet(key, deviceDataList.toSet()) // 리스트를 Set으로 변환하여 저장
        apply()
    }
}

// SharedPreferences에서 BLE 삭제 함수
private fun removeBleFromPreferences(context: Context, myPlantId: String) {
    val sharedPreferences = context.getSharedPreferences("ble_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // 삭제할 알림 ID와 관련된 데이터를 제거합니다.
    editor.remove("ble_$myPlantId")
    editor.apply()
}