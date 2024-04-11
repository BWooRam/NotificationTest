package com.trip.notificationtest.ui.notification

import android.app.PendingIntent
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.trip.notificationtest.R
import com.trip.notificationtest.manager.PushNotificationManagerImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class NotificationTestActivity : AppCompatActivity(R.layout.activity_notification) {
    private var pushNotificationManagerImp: PushNotificationManagerImp? = null
    private var editQuery: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("NotificationTestActivity", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            Log.d("NotificationTestActivity", "Fetching FCM registration token = ${task.result}")
        })

        editQuery = findViewById(R.id.edit_query)

        findViewById<Button>(R.id.button).setOnClickListener {
            Log.d("PushNotificationManagerImp", "Notification Send!")
            val query = editQuery?.text.toString()
            pushNotificationManagerImp?.sendGroupNotification(
                PushNotificationManagerImp.Notification(
                    channelId = query,
                    title = "test",
                    message = "test",
                    group = PushNotificationManagerImp.Group(
                        id = PushNotificationManagerImp.GROUP_STRING,
                        isGroupSummary = true
                    ),
                    contentIntent = getPendingIntent(),
                    style = NotificationCompat.BigTextStyle().bigText("text")
                )
            )
        }

        pushNotificationManagerImp = PushNotificationManagerImp(this)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
            .build()

        val sound = PushNotificationManagerImp.Sound(
            Uri.parse("android.resource://$packageName/${R.raw.test1}"),
            audioAttributes
        )

        val sound1 = PushNotificationManagerImp.Sound(
            Uri.parse("android.resource://$packageName/${R.raw.test2}"),
            audioAttributes
        )

        CoroutineScope(Dispatchers.IO).launch {
            for (count in 0..10) {
                val sound = when (count) {
                    0 -> sound
                    else -> sound1
                }

                pushNotificationManagerImp?.createNotificationChannel(
                    PushNotificationManagerImp.Channel(
                        id = count.toString(),
                        name = count.toString(),
                        sound = sound
                    )
                )
            }
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val contentIntent = Intent(this, NotificationTestActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}