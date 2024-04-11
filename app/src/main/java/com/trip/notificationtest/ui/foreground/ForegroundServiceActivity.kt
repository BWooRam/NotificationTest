package com.trip.notificationtest.ui.foreground

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationManagerCompat
import com.trip.notificationtest.Config
import com.trip.notificationtest.R


/**
 * API별 Foreground Test Activity
 */
class ForegroundServiceActivity : AppCompatActivity(R.layout.activity_main), View.OnClickListener {
    var serviceIntent:Intent? = null
    var serviceIntent2:Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.button).setOnClickListener(this)
        findViewById<SwitchCompat>(R.id.switch1).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button -> {
                serviceIntent = Intent(applicationContext, ForegroundTestService::class.java).apply { action = "startForeground"}
                serviceIntent2 = Intent(applicationContext, ForegroundTestService2::class.java).apply { action = "startForeground"}

                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                    startForegroundService(serviceIntent2)
                } else {
                    startService(serviceIntent)
                    startService(serviceIntent2)
                }

            }

            R.id.switch1 -> {
                Config.isSound = !Config.isSound
                Log.i("isSound","value = ${Config.isSound}")

                val builder = Config.createNotificationBuilder(this)
                val notification = Config.createCustomNotification(this, builder, intent)
                NotificationManagerCompat.from(this).notify(Config.NOTI_ID_DEFAULT, notification)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceIntent)
    }
}
