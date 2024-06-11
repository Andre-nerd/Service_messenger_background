package ru.zoomparty.messenger_example_service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val LOG_SERVICE_CONNECT = "LOG_SERVICE_CONNECT "
class RemoteService : Service() {
    private val iBinder: IBinder = Messenger(IncomingHandler(this)).binder

    override fun onBind(intent: Intent?): IBinder {
        // TODO: Return the communication channel to the service.
        return iBinder
    }
}
class IncomingHandler(context: Context?) : Handler(Looper.getMainLooper()) {
    val service: RemoteService? = context as RemoteService?

    override fun handleMessage(msg: Message) {
        Log.i(LOG_SERVICE_CONNECT,"Service get message $msg")
        try {
            msg.replyTo.send(getCurrentTime(msg))
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun getCurrentTime(msg: Message): Message {
        val dateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.US)
        val resp: Message = Message.obtain()
        val bResp = Bundle()
        bResp.putString(
            "respData", msg.getData().getString("MyString") + " : " + dateFormat.format(Date())
                .toString()
        )
        resp.setData(bResp)
        return resp
    }
}