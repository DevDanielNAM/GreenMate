package com.android.greenmate.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.icu.text.DecimalFormat
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.android.greenmate.domain.BleInterface
import com.android.greenmate.domain.model.DeviceData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleManager @Inject constructor(
    private val context: Context
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private var scanList: SnapshotStateList<DeviceData>? = null
    private var connectedStateObserver: BleInterface? = null
    var bleGatt: BluetoothGatt? = null

    // 현재 연결된 기기
    private var connectedDeviceData: DeviceData? = null
    private var isConnected = false
    private var notificationCharacteristics: List<BluetoothGattCharacteristic> = emptyList()

    // UUIDs from Arduino code
    private val sensorServiceUUID = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214")
    private val lightCharacteristicUUID = UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214")
    private val temperatureCharacteristicUUID = UUID.fromString("19B10002-E8F2-537E-4F6C-D104768A1214")
    private val humidityCharacteristicUUID = UUID.fromString("19B10003-E8F2-537E-4F6C-D104768A1214")
    private val soilMoistureCharacteristicUUID = UUID.fromString("19B10004-E8F2-537E-4F6C-D104768A1214")

    private val scanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("onScanResult", result.toString())
            if (result.device.name != null) {
                var uuid = "null"
                if (result.scanRecord?.serviceUuids != null) {
                    uuid = result.scanRecord!!.serviceUuids.toString()
                }

                val scanItem = DeviceData(
                    result.device.name ?: "null",
                    uuid,
                    result.device.address ?: "null"
                )

                if (!scanList!!.contains(scanItem)) {
                    scanList!!.add(scanItem)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            println("onScanFailed  $errorCode")
        }
    }

    private fun byteArrayToDouble(byteArray: ByteArray): Double {
        // ByteArray의 길이가 8인지 확인
        if (byteArray.size != 8) {
            throw IllegalArgumentException("ByteArray must be 8 bytes long")
        }

        // ByteBuffer를 사용하여 변환
        return ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).double
    }

    private var characteristicQueue: Queue<BluetoothGattCharacteristic> = LinkedList()


    // GATT 연결 시 알림 설정을 저장
    private fun enableNotificationsSequentially(gatt: BluetoothGatt) {
        CoroutineScope(Dispatchers.IO).launch {
            val service: BluetoothGattService? = gatt.getService(sensorServiceUUID)
            service?.let {
                notificationCharacteristics = listOf(
                    it.getCharacteristic(lightCharacteristicUUID),
                    it.getCharacteristic(temperatureCharacteristicUUID),
                    it.getCharacteristic(humidityCharacteristicUUID),
                    it.getCharacteristic(soilMoistureCharacteristicUUID)
                )
                characteristicQueue.addAll(notificationCharacteristics)
                processNextCharacteristic(gatt)
            }
        }
    }

    private fun processNextCharacteristic(gatt: BluetoothGatt) {
        if (characteristicQueue.isNotEmpty()) {
            val characteristic = characteristicQueue.poll()
            CoroutineScope(Dispatchers.IO).launch {
                enableNotification(gatt, characteristic)
            }
        }
    }

    private fun enableNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        CoroutineScope(Dispatchers.IO).launch {
            gatt.setCharacteristicNotification(characteristic, true)
            val descriptor =
                characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }
    }

    private fun disableNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        Log.d("BleManager", "Disabling notification for characteristic: ${characteristic.uuid}")
        CoroutineScope(Dispatchers.IO).launch {
            gatt.setCharacteristicNotification(characteristic, false)
            val descriptor =
                characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            descriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }
    }


    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("BleManager", "Connected")
                    gatt?.discoverServices()
                    // Update the connected device data
                    connectedDeviceData = DeviceData(
                        name = gatt?.device?.name ?: "Unknown",
                        uuid = "",
                        address = gatt?.device?.address ?: "Unknown"
                    )
                    isConnected = true
                    connectedStateObserver?.onConnectedStateObserve(true, "Connected")
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("BleManager", "Disconnected")
                    // Clear the connected device data
                    gatt?.close()
                    bleGatt = null
                    connectedDeviceData = null
                    isConnected = false
                    connectedStateObserver?.onConnectedStateObserve(false, "Disconnected")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                super.onServicesDiscovered(gatt, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("BleManager", "Services Discovered")
                    bleGatt = gatt
                    gatt?.let { enableNotificationsSequentially(it) }
                }
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                super.onDescriptorWrite(gatt, descriptor, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(
                        "BleManager",
                        "Notification enabled for characteristic: ${descriptor?.characteristic?.uuid}"
                    )
                    processNextCharacteristic(gatt!!)
                } else {
                    Log.e(
                        "BleManager",
                        "Failed to enable notification for characteristic: ${descriptor?.characteristic?.uuid}"
                    )
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                super.onCharacteristicChanged(gatt, characteristic)
                Log.d("BleManager", "Characteristic changed: ${characteristic?.uuid}")
                characteristic?.let { processCharacteristic(it) }
            }
        }

        private fun processCharacteristic(characteristic: BluetoothGattCharacteristic) {
            val decimalFormat = DecimalFormat("#.##")
            decimalFormat.roundingMode.apply { RoundingMode.HALF_UP }

            CoroutineScope(Dispatchers.IO).launch {
                when (characteristic.uuid) {
                    lightCharacteristicUUID -> {
                        val lightValue = characteristic.getValue()
                        val doubleLightValue = byteArrayToDouble(lightValue)
                        Log.d("BleManager", "Light: ${decimalFormat.format(doubleLightValue)}")
                        connectedStateObserver?.onSensorValueChanged(
                            "Light",
                            byteArrayToDouble(lightValue).toFloat()
                        )
                    }

                    temperatureCharacteristicUUID -> {
                        val temperatureValue = characteristic.getValue()
                        val doubleTemperatureValue = byteArrayToDouble(temperatureValue)
                        Log.d(
                            "BleManager",
                            "Temperature: ${decimalFormat.format(doubleTemperatureValue)}°C"
                        )
                        connectedStateObserver?.onSensorValueChanged(
                            "Temperature",
                            byteArrayToDouble(temperatureValue).toFloat()
                        )
                    }

                    humidityCharacteristicUUID -> {
                        val humidityValue = characteristic.getValue()
                        val doubleHumidityValue = byteArrayToDouble(humidityValue)
                        Log.d(
                            "BleManager",
                            "Humidity: ${decimalFormat.format(doubleHumidityValue)}%"
                        )
                        connectedStateObserver?.onSensorValueChanged(
                            "Humidity",
                            byteArrayToDouble(humidityValue).toFloat()
                        )
                    }

                    soilMoistureCharacteristicUUID -> {
                        val soilMoistureValue = characteristic.getValue()
                        val doubleSoilMoistureValue = byteArrayToDouble(soilMoistureValue)
                        Log.d(
                            "BleManager",
                            "Soil Moisture: ${decimalFormat.format(doubleSoilMoistureValue)}%"
                        )
                        connectedStateObserver?.onSensorValueChanged(
                            "Soil Moisture",
                            byteArrayToDouble(soilMoistureValue).toFloat()
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startBleScan() {
        CoroutineScope(Dispatchers.IO).launch {
            scanList?.clear()
            val scanFilter = ScanFilter.Builder()
//                .setServiceUuid(ParcelUuid.fromString(sensorServiceUUID.toString()))
                .build()
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()
            val filters = listOf(scanFilter)
            bluetoothLeScanner.startScan(filters, scanSettings, scanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    fun stopBleScan() {
        CoroutineScope(Dispatchers.IO).launch {
            bluetoothLeScanner.stopScan(scanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    fun startBleConnectGatt(deviceData: DeviceData) {
        CoroutineScope(Dispatchers.IO).launch {
            bluetoothAdapter
                .getRemoteDevice(deviceData.address)
                .connectGatt(context, false, gattCallback)
        }
    }

    fun setScanList(pScanList: SnapshotStateList<DeviceData>) {
        scanList = pScanList
    }

    fun onConnectedStateObserve(pConnectedStateObserver: BleInterface) {
        Log.d("BleManager", "Setting connectedStateObserver")
        CoroutineScope(Dispatchers.IO).launch {
            connectedStateObserver = pConnectedStateObserver
            pConnectedStateObserver.onConnectedStateObserve(isConnected, connectedDeviceData?.name ?: "Unknown")
        }
    }


    fun getConnectedDeviceData(): DeviceData? {
        return connectedDeviceData
    }


    fun isDeviceConnected(): Boolean {
        return isConnected
    }

    @SuppressLint("MissingPermission")
    fun disconnectGatt() {
        CoroutineScope(Dispatchers.IO).launch {
            bleGatt?.let { gatt ->
                Log.d("BleManager", "Disconnecting GATT connection")

                // 1. Notifications 해제
                notificationCharacteristics.forEach { characteristic ->
                    disableNotification(gatt, characteristic)
                }

                // 2. 연결 해제
                gatt.disconnect()

                // 3. 콜백 대기
                gattCallback?.let { callback ->
                    // 특정 시간 동안 GATT 해제를 기다림
                    Handler(Looper.getMainLooper()).postDelayed({
                        // 4. GATT 자원 해제
                        gatt.close()
                        bleGatt = null
                        isConnected = false
                        connectedDeviceData = null
                        connectedStateObserver?.onConnectedStateObserve(false, "Disconnected")
                        Log.d("BleManager", "GATT connection closed")
                    }, 500)  // 500ms 정도 기다림
                }
            } ?: run {
                Log.d("BleManager", "No GATT connection to disconnect")
            }
        }
    }
}