package ru.zoomparty.sample_background_service

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import ru.zoomparty.sample_background_service.ui.theme.Sample_background_serviceTheme
import ru.zoomparty.sample_background_service.ui.widgets.Greeting


class MainActivity : ComponentActivity() {

    private var messService: Messenger? = null
    private var isBindService = false

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            messService = Messenger(binder)
            isBindService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            messService = null;
            isBindService = false
        }
    }
    @SuppressLint("HandlerLeak")
    inner class ResponseHandler() : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val counter = msg.data.getInt("counter")
            Log.d(LOG_ACTIVITY, "Activity | fun handleMessage $counter")
            _counter.value = counter.toString()
            if(counter == 0) unBindSampleService()
        }
    }
    private val _counter = MutableStateFlow("")

    private fun bindSampleService() {
        val intent = Intent(this, SampleService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unBindSampleService() {
        if (isBindService) {
            unbindService(serviceConnection)
            isBindService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindSampleService()
        enableEdgeToEdge()
        setContent {
            Sample_background_serviceTheme {
                val counter =_counter.collectAsState()
                Greeting(counter = counter.value) {
                    if (isBindService.not()) {
                        bindSampleService()
                    } else {
                        sendMessageFromActivity()
                    }
                }
            }
        }
    }

    private fun sendMessageFromActivity() {
        val msg: Message = Message.obtain()
        msg.replyTo = Messenger(ResponseHandler())
        val bundle = Bundle()
        bundle.putString("Command", "Start")
        msg.data = bundle
        try {
            messService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unBindSampleService()
    }

    companion object {
        const val LOG_ACTIVITY = "LOG_ACTIVITY "
    }
}

