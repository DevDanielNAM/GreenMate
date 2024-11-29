package com.android.greenmate.presentation

import android.content.Context

fun saveAlarmInfo(context: Context, id: Int, title: String, message: String, day: Int/*, hour: Int, minute: Int*/) {
    val sharedPreferences = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val alarmCount = sharedPreferences.getInt("alarm_count_${id}", 0)

    editor.putInt("alarm_id_${id}_${alarmCount}", id)
    editor.putString("alarm_title_${id}_${alarmCount}", title)
    editor.putString("alarm_message_${id}_${alarmCount}", message)
    editor.putInt("alarm_day_${id}_${alarmCount}", day)
//    editor.putInt("alarm_hour_$alarmCount", hour)
//    editor.putInt("alarm_minute_$alarmCount", minute)
    editor.putInt("alarm_count_${id}", alarmCount + 1) // 알림 개수 증가
    editor.apply()
}