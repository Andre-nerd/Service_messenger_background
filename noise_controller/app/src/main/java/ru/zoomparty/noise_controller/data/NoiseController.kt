package ru.zoomparty.noise_controller.data

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import ru.zoomparty.noise_controller.data.config.temp_file_record
import java.io.File


class NoiseController(private val context: Context) {
     private var audioRecorder: MediaRecorder? = null

    fun start() {
        audioRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
        audioRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(getAudioPath())
            prepare()
            start()
        }
    }

    fun stop() {
        audioRecorder?.let {
            it.stop()
            it.release()
        }
        deleteTempFileRecord()
        audioRecorder = null
    }
    @SuppressLint("MissingPermission")
    fun validateMicAvailability(): Boolean {
        var available = true
        var recorder: AudioRecord? =
            AudioRecord(
                MediaRecorder.AudioSource.MIC, 44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_DEFAULT, 44100
            )
        try {
            if (recorder!!.recordingState != AudioRecord.RECORDSTATE_STOPPED) {
                available = false
            }

            recorder.startRecording()
            if (recorder.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop()
                available = false
            }
            recorder.stop()
        } finally {
            recorder!!.release()
            recorder = null
        }

        return available
    }
    fun isAudioRecording() = audioRecorder != null
    fun getNoise(): Int = audioRecorder?.maxAmplitude ?: -1

    private fun getAudioPath(): String {
        return temp_file_record
    }

    companion object {
        const val NOISE_IN_MIC = "NOISE_IN_MIC"
        fun deleteTempFileRecord() {
            val tempFile: File = File(temp_file_record)
            if (tempFile.exists()) tempFile.delete()
        }
    }
}