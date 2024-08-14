package com.trip.notificationtest.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.trip.notificationtest.R

class PushNotificationManagerImp(
    private val context: Context
) : NotificationManager {
    companion object {
        const val NOTI_CHANNER_ID_DEFAULT = "HT-HOME-INTEGRATED-NOTI"
        const val GROUP_ID = 100
        const val sipNotificationId = 1000 // Q버전 이상에서 사용하는 custom notification channel SIP id.
        const val emergencyNotificationId =
            1001 // Q버전 이상에서 사용하는 custom notification channel 비상 id.\
        const val GROUP_STRING = "com.trip.notificationtest.Test"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    override fun createNotificationChannel(channel: NotificationManager.Channel) {
        //Channel Meta 컨버팅
        val channelMeta = runCatching {
            channel as? Channel
        }.getOrNull()

        //Channel Meta 체크
        if (channelMeta == null) {
            Log.e(javaClass.simpleName, "createNotificationChannel Channel is null")
            return
        }

        //Channel 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelMeta.id) == null) {
            val createChannel = NotificationChannel(
                channelMeta.id,
                channelMeta.name,
                channelMeta.importance ?: android.app.NotificationManager.IMPORTANCE_HIGH
            ).also {
                it.setShowBadge(channelMeta.isShowBadge)

                Log.e(javaClass.simpleName, "createNotificationChannel sound = ${channelMeta.sound}")
                if (channelMeta.sound != null) {
                    it.setSound(channelMeta.sound.uri, channelMeta.sound.attributeSet)
                }
            }
            notificationManager.createNotificationChannel(createChannel)
        } else {
            Log.i(javaClass.simpleName, "createNotificationChannel not create")
        }
    }

    override fun sendNotification(notification: NotificationManager.Notification) {
        //Notification Meta 컨버팅
        val notificationMeta = runCatching {
            notification as? Notification
        }.getOrNull()

        //Notification Meta 체크
        if (notificationMeta == null) {
            Log.e(javaClass.simpleName, "sendNotification Notification is null")
            return
        }

        //Notification 생성
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, notificationMeta.channelId)
        } else {
            NotificationCompat.Builder(context).setChannelId(notificationMeta.channelId)
        }.also { noti ->
            noti.setContentIntent(notificationMeta.contentIntent)
                .setSmallIcon(notificationMeta.smallIcon)
                .setContentTitle(notificationMeta.title)
                .setContentText(notificationMeta.message)
                .setAutoCancel(notificationMeta.autoCancel)
                .setStyle(notificationMeta.style)
                .setVibrate(longArrayOf(0, 1000))
                .priority = NotificationCompat.PRIORITY_HIGH

            if (notificationMeta.sound != null) {
                Log.d(javaClass.simpleName, "sendNotification sound = ${notificationMeta.sound}")
                noti.setSound(notificationMeta.sound.uri)
            }

            if (notificationMeta.group != null) {
                Log.d(javaClass.simpleName, "sendNotification group = ${notificationMeta.group}")
                noti.setGroup(notificationMeta.group.id)
//                noti.setSortKey(System.currentTimeMillis().toString())

                if (notificationMeta.group.groupAlertBehavior != null) {
                    noti.setGroupAlertBehavior(notificationMeta.group.groupAlertBehavior)
                }

                if (notificationMeta.group.isGroupSummary != null) {
                    noti.setGroupSummary(notificationMeta.group.isGroupSummary)
                }
            }
        }
        notificationManager.notify(notificationMeta.id, notificationBuilder.build())
    }

    override fun sendGroupNotification(notification: NotificationManager.Notification) {
        //Notification Meta 컨버팅
        val notificationMeta = runCatching {
            notification as? Notification
        }.getOrNull()

        //Notification Meta 체크
        if (notificationMeta == null) {
            Log.e(javaClass.simpleName, "sendNotification Notification is null")
            return
        }

        val newMessageNotification1 = NotificationCompat.Builder(context, notificationMeta.channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(notificationMeta.title)
            .setContentText("You will not believe...")
            .setSound(notificationMeta.sound?.uri)
//            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
//            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMeta.message))
            .setGroup(GROUP_STRING)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val newMessageNotification2 = NotificationCompat.Builder(context, notificationMeta.channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(notificationMeta.title)
            .setContentText("Please join us to celebrate the...")
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMeta.message))
            .setGroup(GROUP_STRING)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val summaryNotification = NotificationCompat.Builder(context, notificationMeta.channelId)
            .setContentTitle(notificationMeta.title)
            // Set content text to support devices running API level < 24.
            .setContentText("Two new messages")
            .setStyle(NotificationCompat.InboxStyle().setSummaryText("test test"))
            .setSmallIcon(R.drawable.ic_launcher_background)
            // Build summary info into InboxStyle template.
            // Specify which group this notification belongs to.
            .setGroup(GROUP_STRING)
            // Set this notification as the summary for the group.
            .setGroupSummary(true)
            .build()

        NotificationManagerCompat.from(context).apply {
            notify(System.currentTimeMillis().toInt(), newMessageNotification1)
//            notify(sipNotificationId, newMessageNotification2)
            notify(GROUP_ID, summaryNotification)
        }
    }

    override fun sendRemoteNotification(
        remoteView: NotificationManager.RemoteView,
        notification: NotificationManager.Notification
    ) {
        //Notification 및 RemoteView Meta 컨버팅
        val notificationMeta = runCatching {
            notification as? Notification
        }.getOrNull()

        val remoteViewMeta = runCatching {
            remoteView as? RemoteView
        }.getOrNull()

        //Notification 및 RemoteView Meta 체크
        if (notificationMeta == null) {
            Log.e(javaClass.simpleName, "sendNotification Notification is null")
            return
        }

        if (remoteViewMeta == null) {
            Log.e(javaClass.simpleName, "sendNotification remoteViewMeta is null")
            return
        }

        //RemoteView Notification 생성
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, notificationMeta.channelId)
        } else {
            NotificationCompat.Builder(context)
        }.also {
            it.setWhen(System.currentTimeMillis())
                //Set the time that the event occurred. Notifications in the panel are sorted by this time.
                .setSmallIcon(notificationMeta.smallIcon)
                .setCustomContentView(remoteViewMeta.remoteViews)
                .setCustomBigContentView(remoteViewMeta.remoteViews)
                .setAutoCancel(notificationMeta.autoCancel)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(remoteViewMeta.fullScreenIntent, true)
                .setContentIntent(remoteViewMeta.contentIntent)
        }

        notificationManager.notify(notificationMeta.id, notificationBuilder.build())
    }

    data class Notification(
        val id: Int = sipNotificationId,
        val channelId: String = NOTI_CHANNER_ID_DEFAULT,
        @DrawableRes
        val smallIcon: Int = R.mipmap.ic_launcher_round,
        val title: String,
        val message: String,
        val group: Group? = null,
        val autoCancel: Boolean = false,
        val sound: Sound? = null,
        val style: NotificationCompat.Style? = null,
        val contentIntent: PendingIntent? = null
    ) : NotificationManager.Notification

    data class Group(
        val id: String,
        val groupAlertBehavior: Int? = null,
        val isGroupSummary: Boolean? = null
    )

    data class RemoteView(
        val remoteViews: RemoteViews,
        val fullScreenIntent: PendingIntent,
        val contentIntent: PendingIntent
    ) : NotificationManager.RemoteView

    data class Channel(
        val id: String = NOTI_CHANNER_ID_DEFAULT,
        val name: String = NOTI_CHANNER_ID_DEFAULT,
        val importance: Int? = null,
        val sound: Sound? = null,
        val isShowBadge: Boolean = false
    ) : NotificationManager.Channel


    data class Sound(
        val uri: Uri,
        val attributeSet: AudioAttributes
    )
}