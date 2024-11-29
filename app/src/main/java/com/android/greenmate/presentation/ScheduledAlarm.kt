package com.android.greenmate.presentation

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

@SuppressLint("ScheduleExactAlarm")
fun scheduledAlarm(context: Context, reminderId: Int, title: String, message: String, day: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MyReceiver::class.java).apply {
        putExtra("id", reminderId)
        putExtra("title", title)
        putExtra("message", message)
        putExtra("day", day)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminderId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Calendar 객체를 사용하여 지정된 날에 맞게 알람을 설정
    val calendar: Calendar = Calendar.getInstance().apply {
        if(day == 0) {
            add(Calendar.SECOND, 60)
        } else {
            timeInMillis = System.currentTimeMillis()

            // day가 오늘 이후인지 확인하고 설정 (현재 월 기준으로 day 설정)
            if (get(Calendar.DAY_OF_MONTH) <= day) {
                set(Calendar.DAY_OF_MONTH, day)
            } else {
                // 만약 day가 오늘 이전이면 다음 달의 해당 일로 설정
                add(Calendar.MONTH, 1)
                set(Calendar.DAY_OF_MONTH, day)
            }

            // 추가로 필요한 시간, 분 설정 가능
            set(Calendar.HOUR_OF_DAY, 9) // 예시로 오전 9시로 설정
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
    }

    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        // If the time has already passed for today, schedule it for the next day
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }


    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}