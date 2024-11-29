package com.android.greenmate.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.greenmate.data.ble.BleManager
import com.android.greenmate.presentation.ui.screens.AddPlantByCamera
import com.android.greenmate.presentation.ui.screens.AddPlantByCameraResult
import com.android.greenmate.presentation.ui.screens.AddPlantByNameScreen
import com.android.greenmate.presentation.ui.screens.AddPlantScreen
import com.android.greenmate.presentation.ui.screens.BleScanConnectScreen
import com.android.greenmate.presentation.ui.screens.CheckPlantDisease
import com.android.greenmate.presentation.ui.screens.LandingPageScreen
import com.android.greenmate.presentation.ui.screens.MyPlantMainScreen
import com.android.greenmate.presentation.ui.screens.MyPlantScreen
import com.android.greenmate.presentation.ui.screens.PhotoGalleryScreen
import com.android.greenmate.presentation.ui.screens.AddRecordScreen
import com.android.greenmate.presentation.ui.screens.MyPlantDiseaseScreen


@Composable
fun MainPlantNavController(
    bleManager: BleManager,
) {
    val mainPlantNavController =  rememberNavController()
    val pagerState = rememberPagerState(pageCount = { 3 })

    NavHost(navController = mainPlantNavController, startDestination = MainPlantNavItem.MyPlantMain.route) {
        composable(MainPlantNavItem.MyPlantMain.route) { MyPlantMainScreen(mainPlantNavController, bleManager, pagerState) }
        composable(MainPlantNavItem.Landing.route) { LandingPageScreen(mainPlantNavController) }
        composable(MainPlantNavItem.AddPlant.route) { AddPlantScreen(mainPlantNavController) }
        composable(MainPlantNavItem.AddPlantName.route) { AddPlantByNameScreen(null, mainPlantNavController) }
        composable(
            MainPlantNavItem.AddMyPlantRecord.route,
            arguments = listOf(navArgument("myPlantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val myPlantId = backStackEntry.arguments?.getString("myPlantId") ?: ""
            AddRecordScreen(mainPlantNavController, Uri.decode(myPlantId))
        }
        composable(MainPlantNavItem.MyPlant.route) { MyPlantScreen(mainPlantNavController, bleManager) }
        composable(MainPlantNavItem.MyPlantDisease.route) { MyPlantDiseaseScreen(mainPlantNavController) }

        composable(
            CameraNavItem.Camera.route,
            arguments = listOf(navArgument("addOrDisease") { type = NavType.StringType })
        ) { backStackEntry ->
            val addOrDisease = backStackEntry.arguments?.getString("addOrDisease") ?: ""
            AddPlantByCamera(mainPlantNavController, Uri.encode(addOrDisease))
        }
        composable(
            CameraNavItem.Gallery.route,
            arguments = listOf(navArgument("addOrDisease") { type = NavType.StringType })
        ) { backStackEntry ->
            val addOrDisease = backStackEntry.arguments?.getString("addOrDisease") ?: ""
            PhotoGalleryScreen(mainPlantNavController, Uri.encode(addOrDisease))
        }
        composable(
            CameraNavItem.CameraInference.route,
            arguments = listOf(navArgument("uriString") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uriString") ?: ""
            AddPlantByCameraResult(mainPlantNavController, Uri.decode(uriString))
        }

        composable(
            CameraNavItem.CameraDiseaseInference.route,
            arguments = listOf(navArgument("uriString") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uriString") ?: ""
            CheckPlantDisease(mainPlantNavController, Uri.decode(uriString))
        }

        composable(
            BleNavItem.ScanConnect.route,
            arguments = listOf(navArgument("myPlantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val myPlantId = backStackEntry.arguments?.getString("myPlantId") ?: ""
            BleScanConnectScreen(mainPlantNavController, bleManager, Uri.decode(myPlantId))
        }
    }
}

sealed class MainPlantNavItem(val route: String) {
    data object MyPlantMain : MainPlantNavItem("myplant_main")
    data object Landing : MainPlantNavItem("landing")
    data object AddPlant : MainPlantNavItem("add_plant")
    data object AddPlantCamera : MainPlantNavItem("add_plant_camera/{addOrDisease}")
    data object AddPlantName : MainPlantNavItem("add_plant_name")
    data object AddMyPlantRecord : MainPlantNavItem("add_myplant_record/{myPlantId}")
    data object MyPlant : MainPlantNavItem("myplant")
    data object MyPlantDisease : MainPlantNavItem("myplant_disease")
}

sealed class CameraNavItem(val route: String) {
    data object Camera : CameraNavItem("camera/{addOrDisease}")
    data object Gallery : CameraNavItem("gallery/{addOrDisease}")
    data object CameraInference : CameraNavItem("inference/{uriString}")
    data object CameraDiseaseInference : CameraNavItem("disease_inference/{uriString}")
}

sealed class BleNavItem(val route: String) {
    data object ScanConnect : BleNavItem("scan_connect/{myPlantId}")
}