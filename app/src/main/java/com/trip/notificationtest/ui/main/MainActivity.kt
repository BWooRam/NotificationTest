package com.trip.notificationtest.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.trip.notificationtest.Config
import com.trip.notificationtest.NotiService
import com.trip.notificationtest.R


class MainActivity : AppCompatActivity(R.layout.activity_main), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.button).run {
            setOnClickListener(this@MainActivity)
            isEnabled = false
        }
        findViewById<SwitchCompat>(R.id.switch1).setOnClickListener(this)
        Log.d("MyFirebaseMessagingService", "onCreate intent extras title = ${intent?.extras?.getString("title")}")
        Log.d("MyFirebaseMessagingService", "onCreate intent extras body = ${intent?.extras?.getString("body")}")
        Log.d("MyFirebaseMessagingService", "onCreate intent title = ${intent?.getStringExtra("title")}")
        Log.d("MyFirebaseMessagingService", "onCreate intent body = ${intent?.getStringExtra("body")}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createSoundChannel()
            createNotSoundChannel()
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            Log.d("MainActivity", "Fetching FCM registration token = ${task.result}")
        })
    }

    override fun onNewIntent(intent: Intent?) {
        if(intent?.extras?.getBoolean("getCallAcceptIntent") != null)
            Log.d("MainActivity", "onNewIntent getCallAcceptIntent = ${intent?.extras?.getBoolean("getCallAcceptIntent")}")
        if(intent?.extras?.getBoolean("getCallIntent") != null)
            Log.d("MainActivity", "onNewIntent getCallIntent = ${intent?.extras?.getBoolean("getCallIntent")}")
        if(intent?.extras?.getBoolean("getCancelIntent") != null)
            Log.d("MainActivity", "onNewIntent getCancelIntent = ${intent?.extras?.getBoolean("getCancelIntent")}")
        super.onNewIntent(intent)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        Log.d("MainActivity", "startActivityForResult intent = ${intent?.extras}, requestCode = $requestCode")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("MainActivity", "onActivityResult intent = ${intent?.extras}, requestCode = $requestCode")
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button ->  {
                startService(Intent(applicationContext, NotiService::class.java))
                Log.d("MainActivity","R.id.button onClick start")
            }
            R.id.switch1 -> {
                Config.isSound = !Config.isSound
                Log.i("isSound","value = ${Config.isSound}")
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createSoundChannel() {
        Log.i("Build.VERSION_CODES.O", "이상")
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val alramChannel = NotificationChannel(
            "NOTI_SOUND",
            "알림공지",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        alramChannel.description = "Sound Channel Description"
        alramChannel.enableLights(true)
        alramChannel.lightColor = Color.GREEN

        Log.i("Build.VERSION_CODES.O", "isSound True")
        alramChannel.enableVibration(true)
        alramChannel.vibrationPattern = longArrayOf(100, 200, 100, 200)
        val soundUri = Uri.parse(("android.resource://${packageName}/${com.trip.notificationtest.R.raw.test1}"))
        val att = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        alramChannel.setSound(soundUri, att)

        manager.createNotificationChannel(alramChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotSoundChannel() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Log.i("Build.VERSION_CODES.O", "이상")
        val alramChannel = NotificationChannel(
            "NOTI_NOT_SOUND",
            "무음알림공지",
            NotificationManager.IMPORTANCE_HIGH
        )
        alramChannel.description = "Sound Channel Description"
        alramChannel.enableLights(true)
        alramChannel.lightColor = Color.GREEN

        alramChannel.enableVibration(true)
        alramChannel.vibrationPattern = longArrayOf(100, 200, 100, 200)

        manager.createNotificationChannel(alramChannel)
    }
}
