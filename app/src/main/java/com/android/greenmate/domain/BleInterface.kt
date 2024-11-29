package com.android.greenmate.domain

interface BleInterface {
    fun onConnectedStateObserve(isConnected: Boolean, data: String)
    fun onSensorValueChanged(sensorType: String, value: Float)
}