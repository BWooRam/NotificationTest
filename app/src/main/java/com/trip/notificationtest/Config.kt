package com.trip.notificationtest

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.trip.notificationtest.ui.main.MainActivity

object Config {
    var isSound = false

    const val notificationID = "haeroad_service_channel"
    const val NOTI_CHANNER_ID_DEFAULT = "NOTI_CHANNER_ID_DEFAULT"
    const val NOTI_CHANNER_NAME_DEFAULT = "NOTI_CHANNER_NAME_DEFAULT"
    const val NOTI_ID_DEFAULT = 1000


    fun createNotificationBuilder(context: Context): NotificationCompat.Builder{
        val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTI_CHANNER_ID_DEFAULT,
                NOTI_CHANNER_NAME_DEFAULT,
                NotificationManager.IMPORTANCE_HIGH
            )
//            channel.setShowBadge(false)
            if(notificationManager.getNotificationChannel(NOTI_CHANNER_ID_DEFAULT) == null)
                notificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(context, channel.id)
        } else {
            NotificationCompat.Builder(context, NOTI_CHANNER_ID_DEFAULT)
        }
    }

    fun createCustomNotification(context: Context, customNotification: NotificationCompat.Builder, pendingIntent: Intent): Notification {
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_call).apply {
            setOnClickPendingIntent(R.id.btn_cancel, getCancelIntent(context))
            setOnClickPendingIntent(R.id.btn_accept, getCallAcceptIntent(context))
            setOnClickPendingIntent(R.id.ll_back, getCallIntent(context))
            setTextViewText(R.id.tv_title, "테스트 제목")
            setTextViewText(R.id.tv_content, "테스트 내용")
        }

        val fullScreenIntent = Intent(context, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

        return customNotification
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    pendingIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            ).build()
    }

    fun getCallAcceptIntent(context: Context): PendingIntent {
        val customNotificationId = 1000 // Q버전 이상에서 사용하는 custom notification id.
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.cancel(customNotificationId)

        val notificationActivityIntent = Intent(context, MainActivity::class.java).apply {
            putExtras(Bundle().apply { putBoolean("getCallAcceptIntent", true) })
            putExtra("getCallAcceptIntent", true)
        }

        val resultPendingIntent = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(notificationActivityIntent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        return resultPendingIntent
    }

    fun getCallIntent(context: Context): PendingIntent {
        val customNotificationId = 1000 // Q버전 이상에서 사용하는 custom notification id.
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.cancel(customNotificationId)

        val notificationActivityIntent = Intent(context, MainActivity::class.java).apply {
            putExtras(Bundle().apply { putBoolean("getCallIntent", true) })
            putExtra("getCallIntent", true)
        }

        val resultPendingIntent = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(notificationActivityIntent)
            .getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT)

        return resultPendingIntent
    }

    fun getCancelIntent(context: Context): PendingIntent {
        val customNotificationId = NOTI_ID_DEFAULT // Q버전 이상에서 사용하는 custom notification id.
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.cancel(customNotificationId)

        val notificationActivityIntent = Intent(context, MainActivity::class.java).apply {
            putExtras(Bundle().apply { putBoolean("getCancelIntent", true) })
            putExtra("getCancelIntent", true)
        }

        val resultPendingIntent = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(notificationActivityIntent)
            .getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT)

        return resultPendingIntent
    }

}