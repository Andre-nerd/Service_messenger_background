package ru.zoomparty.sample_background_service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SampleService: Service() {
    private val scope  = CoroutineScope(Job() + Dispatchers.Default)
    override fun onCreate() {
        Log.d(LOG_SERVICE, "$this | onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            Log.e(LOG_SERVICE, "$this | onStartCommand() ")
            Thread.sleep(10000)
            withContext(Dispatchers.Main){
                stopSelf()
                Log.d(LOG_SERVICE, "stopSelf() $this")
            }
        }

        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d(LOG_SERVICE, "SampleService | onDestroy()")
    }

    companion object{
        const val LOG_SERVICE  = "LOG_SERVICE"
    }
}