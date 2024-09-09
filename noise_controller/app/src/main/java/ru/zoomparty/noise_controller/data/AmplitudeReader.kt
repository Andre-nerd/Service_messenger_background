package ru.zoomparty.noise_controller.data

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import android.util.Log
import ru.zoomparty.noise_controller.data.NoiseController.Companion.NOISE_IN_MIC
import ru.zoomparty.noise_controller.data.SoundService.Companion.LOG_SOUND_SERVICE
import kotlin.math.abs


@SuppressLint("MissingPermission")
class AmplitudeReader : Thread() {
    private val audioRecord: AudioRecord
    private val bufflen: Int

    init {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
        val channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_DEFAULT
        val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
        bufflen = AudioRecord.getMinBufferSize(SAMPPERSEC, channelConfiguration, audioEncoding)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPPERSEC,
            channelConfiguration,
            audioEncoding,
            bufflen
        )
        audioRecord.startRecording()
    }

    private fun getMax(arr: ShortArray, count: Int): Short {
        var m: Short = 0
        for (i in 0 until count) {
            val c = abs(arr[i].toDouble()).toInt().toShort()
            if (m < c) {
                m = c
            }
        }
        return m
    }

    override fun run() {
        for (i in 0..1000) {
            val curr = ShortArray(bufflen)
            val currread = audioRecord.read(curr, 0, bufflen)
            Log.i(NOISE_IN_MIC, "AmplitudeReader | amplitude is: " + getMax(curr, currread))

        }
        audioRecord.stop()
        audioRecord.release()
    }

    companion object {
        private const val SAMPPERSEC = 48000
    }
}
