
package com.trip.notificationtest.ui.foreground

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.trip.notificationtest.Config
import com.trip.notificationtest.Config.isSound
import com.trip.notificationtest.Config.notificationID
import com.trip.notificationtest.ui.main.MainActivity
import com.trip.notificationtest.R


class ForegroundTestService : Service() {
    companion object {
        const val CHANNEL_ID = "foreground_service_channel"
        const val NONE_CHANNEL_ID = "none_service_channel"
        const val TAG = "ForegroundTestService"
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "intent action = ${intent?.action}")

        if (intent != null && "startForeground" == intent.action) {
            startForegroundService()
            val builder = Config.createNotificationBuilder(this)
            val notification = Config.createCustomNotification(this, builder, intent)
            NotificationManagerCompat.from(this).notify(Config.NOTI_ID_DEFAULT, notification)
        }

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
        
        var builder: NotificationCompat.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel(notificationManager, notificationID)


            /*    val defaultSoundUri: Uri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val r = RingtoneManager.getRingtone(applicationContext, defaultSoundUri)
                r.play()*/

                NotificationCompat.Builder(
                    applicationContext,
                    notificationID
                )
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("title")
                    .setContentText("contents")
                    .setContentIntent(pendingIntent)


            } else {

                val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext)

                builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("title")
                    .setContentText("contents")
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
            }

        startForeground(1, builder.build())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(manager: NotificationManager, id: String) {
        Log.i("Build.VERSION_CODES.O", "이상")
        var channel = NotificationChannel(
            id,
            "포그라운서비스",

            if(isSound) NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Sound Channel Description"
        channel.enableLights(true)
        channel.lightColor = Color.GREEN

        manager.createNotificationChannel(channel)
        Log.i("isSound","new notification id = $id")
    }
}
