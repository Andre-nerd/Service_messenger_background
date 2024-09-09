package ru.zoomparty.noise_controller

import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.zoomparty.noise_controller.data.NoiseController
import ru.zoomparty.noise_controller.data.NoiseController.Companion.NOISE_IN_MIC
import ru.zoomparty.noise_controller.domain.state.DeviceMicState
import ru.zoomparty.noise_controller.domain.state.DeviceVolumeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.zoomparty.noise_controller.data.VolumeController

class SoundViewModel:ViewModel() {
    private val volumeController = VolumeController(App.appContext)
    private val noiseController = NoiseController(App.appContext)


    private val _stateVolume = MutableStateFlow(DeviceVolumeState.INITIAL)
    val stateVolume = _stateVolume.asStateFlow()
    private val _stateNoise = MutableStateFlow(DeviceMicState.INITIAL)
    val stateNoise= _stateNoise.asStateFlow()



    init {
//        viewModelScope.launch(Dispatchers.Default) {
//            while(true){
//                delay(500)
//                getSoundState()
//
//            }
//        }
//        noiseController.start()
//        viewModelScope.launch(Dispatchers.Default) {
//            while (true){
//                delay(100)
//                val noise = noiseController.getNoise()
//                Log.d(NOISE_IN_MIC,"volume in mic $noise")
//                _stateNoise.update { DeviceMicState(noiseLevel = noise) }
//            }
//        }
    }

    private fun getSoundState(){
        val newState = volumeController.getSoundState()
        _stateVolume.update { newState}
    }
    fun upVolume(){
        val newVolume = if(stateVolume.value.currentVolume < stateVolume.value.maxVolume) stateVolume.value.currentVolume + 1 else
            stateVolume.value.maxVolume
        volumeController.upVolume(newVolume)
    }
    fun downVolume(){
        val newVolume = if(stateVolume.value.currentVolume > 0) stateVolume.value.currentVolume - 1 else
            0
        volumeController.downVolume(newVolume)
    }

    override fun onCleared() {
        super.onCleared()
    }
}