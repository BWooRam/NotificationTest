package com.trip.notificationtest.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
        findViewById<Button>(R.id.button).setOnClickListener(this)
        findViewById<SwitchCompat>(R.id.switch1).setOnClickListener(this)
        Log.d("MainActivity", "onCreate intent = $${intent?.extras}")

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
            R.id.button ->  startService(Intent(applicationContext, NotiService::class.java))
            R.id.switch1 -> {
                Config.isSound = !Config.isSound
                Log.i("isSound","value = ${Config.isSound}")
            }
        }

    }
}
