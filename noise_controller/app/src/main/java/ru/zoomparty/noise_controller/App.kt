package ru.zoomparty.noise_controller

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import ru.zoomparty.noise_controller.data.NoiseController.Companion.NOISE_IN_MIC
import ru.zoomparty.noise_controller.data.SoundService

class App:Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    override fun onTerminate() {
        Log.i(NOISE_IN_MIC, "Application onTerminate()")
        SoundService.stopService(App.appContext)
        super.onTerminate()
    }


    companion object{
        lateinit var appContext:Context
    }
}