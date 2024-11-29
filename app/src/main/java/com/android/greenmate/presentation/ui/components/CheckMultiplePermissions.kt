package com.android.greenmate.presentation.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.android.greenmate.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckMultiplePermissions(
    permissionState: MultiplePermissionsState,
    onPermissionResult: (Boolean) -> Unit,
    showPermissionDialog: MutableState<Boolean>
) {
    val context = LocalContext.current
    val permissionDescriptionProviderMap = createPermissionMap()

    when {
        permissionState.allPermissionsGranted -> {
            onPermissionResult(true)
        }
        else -> {
            onPermissionResult(false)
            if (permissionState.revokedPermissions.isNotEmpty()) {
                RevokedPermissionsDialog(
                    permissionState,
                    permissionDescriptionProviderMap,
                    context,
                    showPermissionDialog
                )
            }
        }
    }
}

private fun createPermissionMap() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    mapOf(
        android.Manifest.permission.POST_NOTIFICATIONS to NotificationPermissionDescriptionProvider(),
        android.Manifest.permission.ACCESS_NOTIFICATION_POLICY to NotificationPermissionDescriptionProvider(),
        android.Manifest.permission.SCHEDULE_EXACT_ALARM to NotificationPermissionDescriptionProvider(),
        android.Manifest.permission.RECEIVE_BOOT_COMPLETED to NotificationPermissionDescriptionProvider(),

        android.Manifest.permission.ACCESS_FINE_LOCATION to LocationPermissionDescriptionProvider(),
        android.Manifest.permission.INTERNET to LocationPermissionDescriptionProvider(),

        android.Manifest.permission.BLUETOOTH_SCAN to BluetoothPermissionDescriptionProvider(),
        android.Manifest.permission.BLUETOOTH_ADVERTISE to BluetoothPermissionDescriptionProvider(),
        android.Manifest.permission.BLUETOOTH_CONNECT to BluetoothPermissionDescriptionProvider(),

        android.Manifest.permission.CAMERA to CameraPermissionDescriptionProvider(),

        android.Manifest.permission.READ_MEDIA_IMAGES to StoragePermissionDescriptionProvider(),
        android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED to StoragePermissionDescriptionProvider()
    )
} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    mapOf(
        android.Manifest.permission.ACCESS_NOTIFICATION_POLICY to NotificationPermissionDescriptionProvider(),
        android.Manifest.permission.SCHEDULE_EXACT_ALARM to NotificationPermissionDescriptionProvider(),
        android.Manifest.permission.RECEIVE_BOOT_COMPLETED to NotificationPermissionDescriptionProvider(),

        android.Manifest.permission.ACCESS_FINE_LOCATION to LocationPermissionDescriptionProvider(),
        android.Manifest.permission.INTERNET to LocationPermissionDescriptionProvider(),

        android.Manifest.permission.BLUETOOTH_SCAN to BluetoothPermissionDescriptionProvider(),
        android.Manifest.permission.BLUETOOTH_ADVERTISE to BluetoothPermissionDescriptionProvider(),
        android.Manifest.permission.BLUETOOTH_CONNECT to BluetoothPermissionDescriptionProvider(),

        android.Manifest.permission.CAMERA to CameraPermissionDescriptionProvider(),

        android.Manifest.permission.READ_EXTERNAL_STORAGE to StoragePermissionDescriptionProvider(),
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE to StoragePermissionDescriptionProvider()
    )
} else {
    mapOf(
        android.Manifest.permission.ACCESS_NOTIFICATION_POLICY to NotificationPermissionDescriptionProvider(),
        android.Manifest.permission.RECEIVE_BOOT_COMPLETED to NotificationPermissionDescriptionProvider(),

        android.Manifest.permission.ACCESS_FINE_LOCATION to LocationPermissionDescriptionProvider(),
        android.Manifest.permission.ACCESS_COARSE_LOCATION to LocationPermissionDescriptionProvider(),
        android.Manifest.permission.INTERNET to LocationPermissionDescriptionProvider(),

        android.Manifest.permission.BLUETOOTH to BluetoothPermissionDescriptionProvider(),
        android.Manifest.permission.BLUETOOTH_ADMIN to BluetoothPermissionDescriptionProvider(),

        android.Manifest.permission.CAMERA to CameraPermissionDescriptionProvider(),

        android.Manifest.permission.READ_EXTERNAL_STORAGE to StoragePermissionDescriptionProvider(),
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE to StoragePermissionDescriptionProvider()
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RevokedPermissionsDialog(
    permissionState: MultiplePermissionsState,
    permissionDescriptionProviderProviderMap: Map<String, PermissionDescriptionProvider>,
    context: Context,
    showPermissionDialog: MutableState<Boolean>
) {
    /**
     * 한 번에 하나의 퍼미션만을 처리하기 위한 다이얼로그를 띄워야 하므로 for문 대신 lastOrNull() 처리
     */
    val lastRevokedPermission = permissionState.revokedPermissions.lastOrNull()

    lastRevokedPermission?.let { perm ->
        val descriptionProvider =
            permissionDescriptionProviderProviderMap[perm.permission] ?: return@let
        ShowPermissionDialog(
            permissionState,
            perm.status,
            descriptionProvider,
            context,
            showPermissionDialog
        )
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowPermissionDialog(
    permissionState: MultiplePermissionsState,
    permissionStatus: PermissionStatus,
    descriptionProvider: PermissionDescriptionProvider,
    context: Context,
    showPermissionDialog: MutableState<Boolean>,
) {
    PermissionDialog(
        permissionDescriptionProvider = descriptionProvider,
        isPermanentlyDeclined = !permissionStatus.shouldShowRationale,
        onDismiss = { showPermissionDialog.value = false },
        onOkClick = { permissionState.launchMultiplePermissionRequest() },
        onGoToAppSettingsClick = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
    )
}

interface PermissionDescriptionProvider {
    fun getTitle(context: Context): String
    fun getDescription(context: Context, isPermanentlyDeclined: Boolean): String
}

class NotificationPermissionDescriptionProvider : PermissionDescriptionProvider {
    override fun getTitle(context: Context): String {
        return context.getString(R.string.notification_permission_title)
    }

    override fun getDescription(context: Context, isPermanentlyDeclined: Boolean): String {
        val baseDescription = context.getString(R.string.notification_permission_description)
        return if (isPermanentlyDeclined) {
            "$baseDescription\n${context.getString(R.string.notification_permission_additional_instruction)}"
        } else {
            baseDescription
        }
    }
}

class LocationPermissionDescriptionProvider : PermissionDescriptionProvider {
    override fun getTitle(context: Context): String {
        return context.getString(R.string.location_permission_title)
    }

    override fun getDescription(context: Context, isPermanentlyDeclined: Boolean): String {
        val baseDescription = context.getString(R.string.location_permission_description)
        return if (isPermanentlyDeclined) {
            "$baseDescription\n${context.getString(R.string.location_permission_additional_instruction)}"
        } else {
            baseDescription
        }
    }
}

class BluetoothPermissionDescriptionProvider : PermissionDescriptionProvider {
    override fun getTitle(context: Context): String {
        return context.getString(R.string.bluetooth_permission_title)
    }

    override fun getDescription(context: Context, isPermanentlyDeclined: Boolean): String {
        val baseDescription = context.getString(R.string.bluetooth_permission_description)
        return if (isPermanentlyDeclined) {
            "$baseDescription\n${context.getString(R.string.bluetooth_permission_additional_instruction)}"
        } else {
            baseDescription
        }
    }
}

class CameraPermissionDescriptionProvider : PermissionDescriptionProvider {
    override fun getTitle(context: Context): String {
        return context.getString(R.string.camera_permission_title)
    }

    override fun getDescription(context: Context, isPermanentlyDeclined: Boolean): String {
        val baseDescription = context.getString(R.string.camera_permission_description)
        return if (isPermanentlyDeclined) {
            "$baseDescription\n${context.getString(R.string.camera_permission_additional_instruction)}"
        } else {
            baseDescription
        }
    }
}

class StoragePermissionDescriptionProvider : PermissionDescriptionProvider {
    override fun getTitle(context: Context): String {
        return context.getString(R.string.storage_permission_title)
    }

    override fun getDescription(context: Context, isPermanentlyDeclined: Boolean): String {
        val baseDescription = context.getString(R.string.storage_permission_description)
        return if (isPermanentlyDeclined) {
            "$baseDescription\n${context.getString(R.string.storage_permission_additional_instruction)}"
        } else {
            baseDescription
        }
    }
}