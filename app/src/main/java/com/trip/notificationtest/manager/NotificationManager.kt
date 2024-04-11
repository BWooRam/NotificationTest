package com.trip.notificationtest.manager

interface NotificationManager {
    fun createNotificationChannel(channel: Channel)
    fun sendNotification(notification: Notification)
    fun sendGroupNotification(notification: Notification)
    fun sendRemoteNotification(remoteViews: RemoteView, notification: Notification)

    interface RemoteView
    interface Notification
    interface Channel
}