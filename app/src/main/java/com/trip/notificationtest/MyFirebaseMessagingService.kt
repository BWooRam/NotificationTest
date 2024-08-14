package com.trip.notificationtest

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RawRes
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.trip.notificationtest.localize.LanguageManager
import com.trip.notificationtest.manager.PushNotificationManagerImp
import com.trip.notificationtest.ui.main.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var fileId: String? = null

    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        //서버에서 로컬라이징 관련 Data 내부 Notification Json로 구성
        Log.d(javaClass.simpleName, "From: ${remoteMessage.from}")
        Log.d(javaClass.simpleName, "Data: ${remoteMessage.data}")

        val pushNotificationManager = PushNotificationManagerImp(this@MyFirebaseMessagingService)
        val languageManager = LanguageManager()

        val (title:String, body:String) = when(languageManager.getLanguage()){
            LanguageManager.Language.Japan -> Pair(remoteMessage.data["jp-title"] ?: "", remoteMessage.data["jp-body"] ?: "")
            LanguageManager.Language.Korea -> Pair(remoteMessage.data["kr-title"] ?: "", remoteMessage.data["kr-body"] ?: "")
            LanguageManager.Language.English -> Pair(remoteMessage.data["en-title"] ?: "", remoteMessage.data["en-body"] ?: "")
            LanguageManager.Language.Etc -> Pair(remoteMessage.data["en-title"] ?: "", remoteMessage.data["en-body"] ?: "")
        }

        pushNotificationManager.createNotificationChannel(PushNotificationManagerImp.Channel())
        pushNotificationManager.sendNotification(PushNotificationManagerImp.Notification(title = title, message = body))

        // Check if message contains a data payload.
        /*if (remoteMessage.data.isNotEmpty()) {
            Log.d(javaClass.simpleName, "Message data payload: ${remoteMessage.data}")

            val builder = Config.createNotificationBuilder(this)
            val notification = Config.createCustomNotification(this, builder, Intent())
            NotificationManagerCompat.from(this).notify(Config.NOTI_ID_DEFAULT, notification)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(javaClass.simpleName, "Message Notification Title: ${it.title}")
            Log.d(javaClass.simpleName, "Message Notification Body: ${it.body}")
        }*/


//        initRingTone()
//        stopRing()
//        startRing()

/*        val random = Random.nextInt(0, 2)
        fileId = when (random) {
            0 -> MEDIA_PLAYER_ID_EMERGENCY_ENABLE
            else -> MEDIA_PLAYER_ID_EMERGENCY_DISABLE
        }

        startMediaPlayer()
        initMediaPlayer(R.raw.emer_enable_ok)
        initMediaPlayer(R.raw.emer_disable_ok)*/
        Log.d(javaClass.simpleName, "MyFirebaseMessagingService onMessageReceived Start")
        startActivity(Intent(applicationContext, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    companion object {
        @kotlin.jvm.JvmField
        var ringtoneSound: Ringtone? = null

        @kotlin.jvm.JvmField
        var audioManager: AudioManager? = null

        @kotlin.jvm.JvmField
        val mediaPlayerHashMap = hashMapOf<String, MediaPlayer>()
        private var soundRepeatCount = 0

        const val MEDIA_PLAYER_ID1 = "media_player_id1"
        const val MEDIA_PLAYER_ID2 = "media_player_id2"
    }

    private fun initRingTone() {
        try {
            if (ringtoneSound != null) {
                stopRing()
            }

            ringtoneSound = RingtoneManager.getRingtone(
                applicationContext,
                RingtoneManager.getActualDefaultRingtoneUri(
                    applicationContext,
                    RingtoneManager.TYPE_RINGTONE
                )
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    isLooping
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startRing() {
        try {
            ringtoneSound!!.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun stopRing() {
        try {
            ringtoneSound?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initMediaPlayer(@RawRes res: Int) {
        val id = when (res) {
            R.raw.test1 -> MEDIA_PLAYER_ID1
            R.raw.test2 -> MEDIA_PLAYER_ID2
            else -> return
        }

        if(mediaPlayerHashMap[id] != null)
            return

        try {
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?

            val mediaPlayer = MediaPlayer.create(this, res)
            mediaPlayer?.run {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                setOnCompletionListener { mp -> // emergency 음원 3번 반복
                    Log.d(
                        this@MyFirebaseMessagingService.javaClass.simpleName,
                        "initMediaPlayer setOnCompletionListener soundRepeatCount = $soundRepeatCount"
                    )
                    if (++soundRepeatCount < 3) {
                        mp.prepareAsync()
                    } else {
                        releaseMediaPlayer()
                    }
                }

                setOnPreparedListener {
                    Log.d(
                        this@MyFirebaseMessagingService.javaClass.simpleName,
                        "initMediaPlayer setOnPreparedListener"
                    )
                    it.start()
                }
            }
            mediaPlayerHashMap[id] = mediaPlayer
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun releaseMediaPlayer() {
        if (soundRepeatCount <= 0) {
            return
        }

        try {
            Log.d(javaClass.simpleName, "releaseMediaPlayer start")
            allRelease()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun stopMediaPlayer() {
        try {
            soundRepeatCount = 0
            allStop()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun startMediaPlayer() {
        try {
            val currentMediaPlayer = mediaPlayerHashMap[fileId] ?: return

            if (isPlay()) {
                stopMediaPlayer()
                currentMediaPlayer.prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun isPlay(): Boolean {
        for (mediaPlayer in mediaPlayerHashMap.values) {
            if (mediaPlayer.isPlaying)
                return true
        }

        return false
    }

    private fun allStop() {
        for (mediaPlayer in mediaPlayerHashMap.values) {
            if (mediaPlayer.isPlaying)
                mediaPlayer.stop()
        }
    }

    private fun allRelease() {
        for (mediaPlayer in mediaPlayerHashMap.values) {
            if (mediaPlayer.isPlaying)
                mediaPlayer.release()
        }
    }

    override fun onNewToken(token: String) {

    }

}