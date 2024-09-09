package ru.zoomparty.noise_controller.data

import android.content.Context
import android.media.AudioManager
import kotlinx.coroutines.flow.update
import ru.zoomparty.noise_controller.App
import ru.zoomparty.noise_controller.domain.state.DeviceVolumeState

class VolumeController(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
    fun getSoundState():DeviceVolumeState{
        val mVolume: Int = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: -1
        val cValue: Int = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: -1
        return DeviceVolumeState(
            maxVolume = mVolume,
            currentVolume = cValue
        )
    }
    fun upVolume(newVolume:Int){
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
    }
    fun downVolume(newVolume: Int){
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
    }
}