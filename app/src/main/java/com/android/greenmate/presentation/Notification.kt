package com.android.greenmate.presentation

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.android.greenmate.MainActivity
import com.android.greenmate.R

fun NotificationManager.sendNotification(notificationId: Int, title: String, message: String, context: Context) {
    // 알림 클릭 시 실행될 인텐트 생성
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    // PendingIntent 생성
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notificationBuilder = NotificationCompat.Builder(context, "greenmate_channel_id")
        .setSmallIcon(R.mipmap.ic_launcher_round) // 알림 아이콘
        .setContentTitle(title) // 알림 제목
        .setContentText(message) // 알림 내용
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    notify(notificationId, notificationBuilder.build())
}
