package com.android.greenmate.presentation

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class MyReceiver : BroadcastReceiver() {
    private lateinit var notificationManager: NotificationManager


    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        val id = intent.getIntExtra("id",0)
        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")
        val day = intent.getIntExtra("day", -1)
        Log.d("id/title/message/day","${id}/${title}/${message}/${day}")
        //알림을 만들어 보냅니다
        if (title != null && message != null) {
            notificationManager.sendNotification(id, title, message, context)
            scheduledAlarm(context, id, title, message, day)
            saveAlarmInfo(context, id, title, message, day)
        }
    }
}