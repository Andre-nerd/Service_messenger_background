package ru.zoomparty.noise_controller.data

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class MicAudioController {
    @SuppressLint("MissingPermission")
    val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC, 16,
        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
        640
    )

    fun run() {

        try {

            audioRecord.startRecording()
            val outData = ByteArray(640)
            try {
                while (true) {
                    // read audio data from internal mic
                    audioRecord.read(outData, 0, outData.size);
                }
            } finally {
                audioRecord.stop();
            }
        } finally {
            audioRecord.release();

        }
    }
}