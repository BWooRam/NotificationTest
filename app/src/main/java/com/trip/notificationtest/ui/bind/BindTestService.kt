package com.trip.notificationtest.ui.bind

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.trip.notificationtest.Config.isSound
import com.trip.notificationtest.Config.notificationID
import com.trip.notificationtest.ui.main.MainActivity
import com.trip.notificationtest.R
import java.util.*


class BindTestService : Service() {
    inner class LocalBinder : Binder() {
        fun getService(): BindTestService = this@BindTestService
    }

    private val binder = LocalBinder()

    // Random number generator
    private val mGenerator = Random()
    private val isSelfStop = false

    var mBeforeCount = 0
    /** method for clients  */
    var randomNumber: Int = 0
        get(){
            mBeforeCount = field
            field = mGenerator.nextInt(100)
            return field
        }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Work Service!!")
        startForegroundService()
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        if (isSelfStop)
            stopSelf()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
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

                val builder: NotificationCompat.Builder =
                    NotificationCompat.Builder(applicationContext)

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

            if (isSound) NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Sound Channel Description"
        channel.enableLights(true)
        channel.lightColor = Color.GREEN

        manager.createNotificationChannel(channel)
        Log.i("isSound", "new notification id = $id")
    }

    companion object {
        const val TAG = "BindTestService"
    }
}
