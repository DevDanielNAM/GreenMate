package com.android.greenmate.presentation.ui.components

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.android.greenmate.presentation.MyReceiver

fun removeAlarm(context: Context, reminderId: Int) {
    // 해당 reminderId에 대한 알림을 삭제하는 로직을 구현합니다.
    // 예를 들어, AlarmManager를 사용하여 알림을 삭제할 수 있습니다.
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MyReceiver::class.java).apply {
        putExtra("reminderId", reminderId)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminderId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // sharedPreferences 삭제
    removeReminderFromPreferences(context, reminderId)

    // 알림을 취소합니다.
    alarmManager.cancel(pendingIntent)
}

private fun removeReminderFromPreferences(context: Context, reminderId: Int) {
    val sharedPreferences = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val alarmCount = sharedPreferences.getInt("alarm_count_${reminderId}", 0)

    // 알림의 개수에 따라 삭제
    for (i in 0 until alarmCount) {
        editor.remove("alarm_id_${reminderId}_${i}")
        editor.remove("alarm_title_${reminderId}_${i}")
        editor.remove("alarm_message_${reminderId}_${i}")
        editor.remove("alarm_day_${reminderId}_${i}")
    }

    // 알림 개수 항목 삭제
    editor.remove("alarm_count_${reminderId}")

    // 변경 사항 저장
    editor.apply()
}