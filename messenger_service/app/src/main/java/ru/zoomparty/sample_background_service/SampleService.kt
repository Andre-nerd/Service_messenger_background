package ru.zoomparty.sample_background_service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log


class SampleService: Service() {
    private var counter = 10
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private var messService: Messenger = Messenger(IncomingHandler())
    private var messActivity:Messenger? = null

    override fun onBind(intent: Intent?): IBinder {
        if(!isRunning){
            isRunning = true
        }
        return messService.binder
    }

    @SuppressLint("HandlerLeak")
    inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            messActivity = msg.replyTo
            val command = msg.data.getString("Command") ?: "unknown"
            when(command){
                "Start" -> {
                    startCount()
                }
            }
        }
    }

    fun getCurrentProgress():Int{
        return counter
    }
    fun restartCountDown():Int{
        counter = 10
        return counter
    }

    private fun startCount(){
        Log.e(LOG_SERVICE_CONNECT,"SERVICE | fun startCount() started")
        handler.post(object :Runnable{
            override fun run() {
                if(counter > 0){
                    handler.postDelayed(this, 1000L)
                    counter--
                    sendMessageFromService(counter)
                } else {
                    isRunning = false
                }
            }
        })
    }
    fun sendMessageFromService(counter:Int){
        try {
            val resp: Message = Message.obtain()
            val bResp = Bundle()
            bResp.putInt("counter",counter)
            resp.setData(bResp)
            messActivity?.send(resp)
        } catch (e:Throwable) {
            Log.d(LOG_SERVICE_CONNECT, "Error when send message  $e")
        }

    }

    companion object{
        const val LOG_SERVICE_CONNECT  = "LOG_SERVICE_CONNECT"
    }
}