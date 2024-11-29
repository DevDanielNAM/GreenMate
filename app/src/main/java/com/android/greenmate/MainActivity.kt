package com.android.greenmate

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.greenmate.data.ble.BleManager
import com.android.greenmate.presentation.ui.screens.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var bleManager: BleManager

    private val permissionArray = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                android.Manifest.permission.SCHEDULE_EXACT_ALARM
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                android.Manifest.permission.SCHEDULE_EXACT_ALARM
            )
        }
        else -> {
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_NOTIFICATION_POLICY
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkAndRequestExactAlarmPermission()
        }

        // Check if all permissions are granted
        val allPermissionsGranted = permissionArray.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            setContent {
                MainScreen(bleManager)
            }
        } else {
            requestPermissionLauncher.launch(permissionArray)
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkAndRequestExactAlarmPermission() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            Intent().also { intent ->
                intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
        }
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        val name = "greenmate"
        val CHANNEL_ID = "greenmate_channel_id"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }



    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check each permission individually and log the result
        permissions.entries.forEach { entry ->
            Log.d("PermissionCheck", "${entry.key}: ${entry.value}")
        }

        // Separate Bluetooth permissions check for Android 12 and above
        val bluetoothPermissionsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions[android.Manifest.permission.BLUETOOTH_SCAN] == true &&
                    permissions[android.Manifest.permission.BLUETOOTH_CONNECT] == true &&
                    permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        } else {
            permissions[android.Manifest.permission.BLUETOOTH] == true &&
                    permissions[android.Manifest.permission.BLUETOOTH_ADMIN] == true &&
                    permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }

        // Check other essential permissions
        val cameraPermissionGranted = permissions[android.Manifest.permission.CAMERA] == true

        val notificationPermissionsGranted = if (Build.VERSION.SDK_INT >= 33) {
            permissions[android.Manifest.permission.POST_NOTIFICATIONS] == true &&
                    permissions[android.Manifest.permission.ACCESS_NOTIFICATION_POLICY] == true
        } else {
            permissions[android.Manifest.permission.ACCESS_NOTIFICATION_POLICY] == true
        }

        if (bluetoothPermissionsGranted && cameraPermissionGranted && notificationPermissionsGranted) {
            setContent {
                MainScreen(bleManager)
            }
        } else {
            // Show specific message about which permissions are missing
            val missingPermissions = mutableListOf<String>()
            if (!bluetoothPermissionsGranted) missingPermissions.add("블루투스")
            if (!cameraPermissionGranted) missingPermissions.add("카메라")
            if (!notificationPermissionsGranted) missingPermissions.add("알림")

            Toast.makeText(
                this,
                "다음 권한이 필요합니다: ${missingPermissions.joinToString()}",
                Toast.LENGTH_LONG
            ).show()

            // Still show the main screen but with limited functionality
            setContent {
                MainScreen(bleManager)
            }
        }
    }
}

