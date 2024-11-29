package com.android.greenmate.presentation.ui.screens


import androidx.compose.runtime.Composable
import com.android.greenmate.data.ble.BleManager
import com.android.greenmate.presentation.navigation.MainPlantNavController

@Composable
fun MainScreen(bleManager: BleManager) {
    MainPlantNavController(bleManager)
}
