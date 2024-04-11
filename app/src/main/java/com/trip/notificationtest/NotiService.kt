package com.trip.notificationtest

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.trip.notificationtest.Config.isSound
import com.trip.notificationtest.Config.notificationID
import com.trip.notificationtest.ui.main.MainActivity


class NotiService : Service() {
    companion object {
        const val CHANNEL_ID = "service_channel"
        const val NONE_CHANNEL_ID = "none_service_channel"
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun startForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val remoteViews = RemoteViews(packageName, R.layout.notification)

      
        
        var builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel(notificationManager, notificationID)

            /*    val defaultSoundUri: Uri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val r = RingtoneManager.getRingtone(applicationContext, defaultSoundUri)
                r.play()*/

                Notification.Builder(
                    applicationContext,
                    notificationID
                )
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("title")
                    .setContentText("contents")
                    .setAutoCancel(true)
                    .setTicker("Ticker!")
                    .setContentIntent(pendingIntent)


            } else {

                val builder: Notification.Builder = Notification.Builder(applicationContext)

                builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("title")
                    .setContentText("contents")
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setTicker("Ticker!")
                    .setContentIntent(pendingIntent)

                if (isSound) {
                    val alarmSound =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    builder.setSound(alarmSound)
                    builder.setVibrate(longArrayOf(100, 200, 100, 200))
                }

                builder
            }

        notificationManager.notify(1000, builder.build())

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(manager: NotificationManager, id: String) {
        Log.i("Build.VERSION_CODES.O", "이상")
        var alramChannel = NotificationChannel(
            id,
            "알림공지",

            if(isSound) NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_LOW
        )
        alramChannel.description = "Sound Channel Description"
        alramChannel.enableLights(true)
        alramChannel.lightColor = Color.GREEN

        if (isSound) {
            Log.i("Build.VERSION_CODES.O", "isSound True")
            alramChannel.enableVibration(true)
            alramChannel.vibrationPattern = longArrayOf(100, 200, 100, 200)

            val defaultSoundUri: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val att = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            alramChannel.setSound(defaultSoundUri, att)
        }
        manager.createNotificationChannel(alramChannel)
        Log.i("isSound","new notification id = $id")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNoneSoundAndSoundChannel(manager: NotificationManager) {
        Log.i("Build.VERSION_CODES.O", "이상")
        var soundChannel = NotificationChannel(
            NotiService.CHANNEL_ID,
            "알림공지",

            NotificationManager.IMPORTANCE_DEFAULT
        )
        soundChannel.description = "Sound Channel Description"
        soundChannel.enableLights(true)
        soundChannel.lightColor = Color.GREEN
        soundChannel.enableVibration(true)
        soundChannel.vibrationPattern = longArrayOf(100, 200, 100, 200)

        val defaultSoundUri: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val att = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundChannel.setSound(defaultSoundUri, att)

        var noneSoundChannel = NotificationChannel(
            NotiService.NONE_CHANNEL_ID,
            "비알림공지",
            NotificationManager.IMPORTANCE_LOW
        )
        noneSoundChannel.description = "None Sound Channel Description"
        noneSoundChannel.enableLights(true)
        noneSoundChannel.lightColor = Color.GREEN
        noneSoundChannel.enableVibration(false)
        noneSoundChannel.setSound(null, null)

        manager.createNotificationChannel(soundChannel)
        manager.createNotificationChannel(noneSoundChannel)
    }
}
