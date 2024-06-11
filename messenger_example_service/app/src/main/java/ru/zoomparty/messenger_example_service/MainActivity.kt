package ru.zoomparty.messenger_example_service

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
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import ru.zoomparty.messenger_example_service.ui.theme.Messenger_example_serviceTheme


class MainActivity : ComponentActivity() {
    private var serviceConnector:ServiceConnector? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, RemoteService::class.java)
        serviceConnector = ServiceConnector()
        bindService(intent, serviceConnector!!, BIND_AUTO_CREATE)
        enableEdgeToEdge()
        setContent {
            Messenger_example_serviceTheme {
                Button(onClick = { sendMessage() }) {
                    Text(text = "Send message")
                }

            }
        }
    }

    private fun sendMessage() {
        val msg: Message = Message.obtain()
        msg.replyTo = Messenger(ResponseHandler(this))
        val bundle = Bundle()
        bundle.putString("MyString", "Time")
        msg.setData(bundle)
        try {
            serviceConnector?.messenger?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}

class ResponseHandler(private val context: Context?) : Handler(Looper.getMainLooper()) {
//    var mainActivity: MainActivity? = context as MainActivity?
    override fun handleMessage(msg: Message) {
        val data = msg.data
        val dataString = data.getString("respData")
        Toast.makeText(context, dataString, Toast.LENGTH_SHORT).show()
    }
}

class ServiceConnector : ServiceConnection {
    var messenger: Messenger? = null
        private set

    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
        this.messenger = Messenger(iBinder)
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
        this.messenger = null
    }
}