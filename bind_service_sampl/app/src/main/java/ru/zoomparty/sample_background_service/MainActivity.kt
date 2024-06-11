package ru.zoomparty.sample_background_service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.zoomparty.sample_background_service.ui.theme.Sample_background_serviceTheme
import ru.zoomparty.sample_background_service.ui.widgets.Greeting

class MainActivity : ComponentActivity() {
    private var service: SampleService? = null
    private var isBindService = false
    private val handler = Handler(Looper.getMainLooper())

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SampleService.LocalBinder
            this@MainActivity.service = binder.getService()
            isBindService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBindService = false
        }
    }

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
        enableEdgeToEdge()
        setContent {
            Sample_background_serviceTheme {

                Greeting(modifier = Modifier.padding(16.dp)) {
                    Log.i(SampleService.LOG_SERVICE, "onClick ")
                    if (isBindService) {
                        service?.restartCountDown()
                    } else {
                        startPolling()
                    }

                }
            }
        }
    }

    private fun startPolling() {
        if (isBindService.not()) {
            bindSampleService()
        }
        handler.post(object : Runnable {
            override fun run() {
                if (isBindService) {
                    val counter = service?.getCurrentProgress()
                    if (counter!! > 0) {
                        Log.d(SampleService.LOG_SERVICE, "Counter = $counter")
                        handler.postDelayed(this, 1000L)
                    } else {
                        Log.d(SampleService.LOG_SERVICE, "Counter FINISHED")
                    }
                } else {
                    handler.postDelayed(this, 10L)
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unBindSampleService()
    }
}

