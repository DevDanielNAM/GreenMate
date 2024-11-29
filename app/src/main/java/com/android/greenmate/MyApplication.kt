package com.android.greenmate

import android.app.Application
import android.content.Context
import com.android.greenmate.data.ble.BleManager
import com.android.greenmate.data.datasource.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application()
