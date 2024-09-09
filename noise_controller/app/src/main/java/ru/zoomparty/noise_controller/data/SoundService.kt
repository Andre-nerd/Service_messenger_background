package ru.zoomparty.noise_controller.data

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioRecord
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.zoomparty.noise_controller.App
import ru.zoomparty.noise_controller.MainActivity
import ru.zoomparty.noise_controller.data.NoiseController.Companion.NOISE_IN_MIC
import ru.zoomparty.noise_controller.data.config.TIME_DELAY
import ru.zoomparty.noise_controller.domain.state.DeviceMicState
import ru.zoomparty.noise_controller.domain.state.DeviceVolumeState


class SoundService: Service() {
    private lateinit var notificationManager: NotificationManager
    private val scope  = CoroutineScope(Job() + Dispatchers.Default)
    private var volumeController:VolumeController? = null
    private var noiseController:NoiseController? = null
    private var isStarted = false


    override fun onCreate() {
        Log.d(LOG_SOUND_SERVICE, "$this | onCreate()")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        volumeController = VolumeController(this)
        noiseController = NoiseController(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            makeForeground()
            startVolumeController()
            startMoiseController()
//            val amplitudeReader = AmplitudeReader()
//            amplitudeReader.start()
            isStarted = true
        }
        val demoString = intent?.getStringExtra(EXTRA_DEMO) ?: ""
        if(demoString == "stop"){
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d(LOG_SOUND_SERVICE, "SampleService | onDestroy()")
        isStarted = false
    }
    private fun startVolumeController() {
        scope.launch(Dispatchers.Default) {
            while (true) {
                delay(TIME_DELAY)
                val newState = volumeController?.getSoundState() ?: DeviceVolumeState.INITIAL
                DataHolder.updateStateVolume(newState)
            }
        }
    }
    private fun startMoiseController() {
        noiseController?.start()
        scope.launch(Dispatchers.Default) {
            while (true) {
                delay(100)
                val noise = noiseController?.getNoise() ?: -1
                Log.d(NOISE_IN_MIC, "volume in mic $noise | controller ${noiseController?.validateMicAvailability()}")
                DataHolder.updateStateNoise(DeviceMicState(noiseLevel = noise))
            }
        }
    }
    @SuppressLint("ForegroundServiceType")
    private fun makeForeground() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        // before calling startForeground, we must create a notification and a corresponding
        // notification channel

        createServiceNotificationChannel()
        val notification: android.app.Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Foreground Service demonstration")
//            .setSmallIcon(R.drawable.ic_your_app_logo)
            .setContentIntent(pendingIntent)
            .build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                ONGOING_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        }else {
            startForeground(
                ONGOING_NOTIFICATION_ID,
                notification);
        }
    }
    private fun createServiceNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Car noise controller",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.lockscreenVisibility = android.app.Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
    }

    companion object{
        private const val ONGOING_NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "9007"
        const val LOG_SOUND_SERVICE  = "LOG_SOUND_SERVICE"

        private const val EXTRA_DEMO = "EXTRA_DEMO"
        fun startService(context: Context, demoString: String) {
            val intent = Intent(context, SoundService::class.java)
            intent.putExtra(EXTRA_DEMO, demoString)
            context.startForegroundService(intent)
        }
        fun stopService(context: Context) {
            val intent = Intent(context, SoundService::class.java)
            context.stopService(intent)
        }
    }
}