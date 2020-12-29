package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 0
const val FILENAME_EXTRA = "filename_extra"
const val STATUS_EXTRA = "status_extra"

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    pendingIntent: PendingIntent
) {

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(messageBody)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_assistant_black_24dp, "Check the status", pendingIntent)

    notify(NOTIFICATION_ID, builder.build())
}

