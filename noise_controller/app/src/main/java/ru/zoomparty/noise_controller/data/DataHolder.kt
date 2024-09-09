package ru.zoomparty.noise_controller.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.zoomparty.noise_controller.domain.state.DeviceMicState
import ru.zoomparty.noise_controller.domain.state.DeviceVolumeState

object DataHolder {
    private val _stateVolume = MutableStateFlow(DeviceVolumeState.INITIAL)
    val stateVolume = _stateVolume.asStateFlow()
    private val _stateNoise = MutableStateFlow(DeviceMicState.INITIAL)
    val stateNoise= _stateNoise.asStateFlow()

    fun updateStateVolume(state: DeviceVolumeState){
        _stateVolume.update { state }
    }
    fun updateStateNoise(state: DeviceMicState){
        _stateNoise.update { state }
    }
}