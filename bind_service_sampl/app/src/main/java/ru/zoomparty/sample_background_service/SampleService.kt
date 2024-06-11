package ru.zoomparty.sample_background_service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class SampleService: Service() {
    private val binder = LocalBinder()
    private val scope  = CoroutineScope(Job() + Dispatchers.Default)
    private val handler = Handler(Looper.getMainLooper())
    private var counter = 10
    private var isRunning = false

    var messenger: Messenger = Messenger(IncomingHandler())

    val TASK_1 = 1
    val TASK_RESPONSE_1 = 2
    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            val message: Message
            val bundle = Bundle()
            val messageText: String

            when (msg.what) {
                TASK_1 -> {
                    messageText = msg.getData().getString("message").toString()

                    message = Message.obtain(null, TASK_RESPONSE_1)
                    Toast.makeText(
                        getApplicationContext(),
                        "Пришло с Activity: $messageText",
                        Toast.LENGTH_SHORT
                    ).show()

                    bundle.putString("message_res", messageText)
                    message.setData(bundle)
                    val activityMessenger: Messenger = msg.replyTo
                    try {
                        activityMessenger.send(message)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    inner class LocalBinder():Binder(){
        fun getService():SampleService = this@SampleService
    }


    override fun onCreate() {
        Log.d(LOG_SERVICE, "$this | onCreate()")
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        scope.launch {
//            Log.e(LOG_SERVICE, "$this | onStartCommand() ")
//            Thread.sleep(10000)
//            withContext(Dispatchers.Main){
//                stopSelf()
//                Log.d(LOG_SERVICE, "stopSelf() $this")
//            }
//        }
//
//        return START_NOT_STICKY
//    }

    override fun onBind(intent: Intent?): IBinder {
        if(!isRunning){
            startCount()
            isRunning = true
        }
        return messenger.binder
    }

    override fun onDestroy() {
        Log.d(LOG_SERVICE, "SampleService | onDestroy()")
    }

    fun getCurrentProgress():Int{
        return counter
    }
    fun restartCountDown():Int{
        counter = 10
        return counter
    }

    private fun startCount(){
        handler.post(object :Runnable{
            override fun run() {
                if(counter > 0){
                    handler.postDelayed(this, 1000L)
                    counter--
                } else {
                    isRunning = false
                }
            }

        })

    }

    companion object{
        const val LOG_SERVICE  = "LOG_SERVICE"
    }
}