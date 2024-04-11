package com.trip.notificationtest.ui.bind

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.trip.notificationtest.R


/**
 * BindService
 * 서비스가 시작되고 Bind를 허용했으면 stopSelf 또는 stopService를 무조건 해줘야한다.
 * 서비스가 실행되고 클라이언트에서 모두 바인드를 해제해도 서비스를 소멸시키지 않기 때문인다.
 *
 * 참고 : https://developer.android.com/guide/components/bound-services?hl=ko
 */
class BindServiceActivity : AppCompatActivity(R.layout.activity_bind_service),
    View.OnClickListener {
    private val TAG = javaClass.simpleName
    private var mBindService: BindTestService? = null
    private var mBound = false

    //바인딩 서비스 하기전 서비스 시작할 것인지 Flag
    private var isServiceStart = true

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d(TAG, "onService Connected Start!")

            val binder = service as BindTestService.LocalBinder
            mBindService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "onService Disconnected Start!")
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.countBindServiceButton).setOnClickListener(this)
        findViewById<Button>(R.id.unbindServiceButton).setOnClickListener(this)
        findViewById<Button>(R.id.foregroundServiceButton).setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        executeBindService()
    }

    override fun onStop() {
        super.onStop()
        executeUnBindService()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.foregroundServiceButton -> {
                val serviceIntent = Intent(applicationContext, BindTestService::class.java).apply {
                    action = "startForeground"
                }

                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            }

            R.id.countBindServiceButton -> {
                if (mBound && mBindService != null) {
                    mBindService!!.randomNumber?.let {
                        Toast.makeText(this, "Count = $it, Before Count = ${mBindService?.mBeforeCount}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d(TAG, "BindService not exist! execute BindService~~")
                    executeBindService()
                }
            }

            R.id.unbindServiceButton -> {
                executeUnBindService()
            }
        }
    }

    /**
     * bindService 및 startService 실행
     */
    private fun executeBindService() {
        Intent(this, BindTestService::class.java).also { intent ->
            if (isServiceStart)
                startService(intent)

            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * unbindService가 호출되면 onUnbind 메서드가 실행되고 onDestory로 넘어가게 된다. startService를 실행한 상태에서 바인드를 하면 Destory가 되지 않는다(stopSelf or stopService를 해야한다)
     */
    private fun executeUnBindService() {
        if (mBound) {
            Log.d(TAG, "unBindService Start!")
            unbindService(connection)
            mBound = false
        } else
            Log.d(TAG, "BindService not exist!")
    }
}
