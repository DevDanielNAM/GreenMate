package com.android.greenmate.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device booted, restoring alarms...")
            // 저장된 알림 정보를 불러와서 알림을 다시 예약합니다
            restoreAlarms(context)
        }
    }

    private fun restoreAlarms(context: Context) {
        val sharedPreferences = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)

        // 모든 알람 id들을 가져오기 (모든 id에 대해 반복)
        val allIds = sharedPreferences.all.keys
            .filter { it.startsWith("alarm_id_") }
            .map { it.split("_")[2].toInt() }
            .distinct()

        // 각 id에 대해 저장된 알람을 복원
        for (id in allIds) {
            val alarmCount = sharedPreferences.getInt("alarm_count_$id", 0)

            for (i in 0 until alarmCount) {
                val title = sharedPreferences.getString("alarm_title_${id}_$i", null)
                val message = sharedPreferences.getString("alarm_message_${id}_$i", null)
                val day = sharedPreferences.getInt("alarm_day_${id}_$i", -1)

                if (title != null && message != null) {
                    // 알림 예약
                    scheduledAlarm(context, id, title, message, day)
                }
            }
        }
    }

}
