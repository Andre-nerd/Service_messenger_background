package ru.zoomparty.noise_controller.domain.state

data class DeviceVolumeState (
    val maxVolume:Int,
    val currentVolume:Int
){
    companion object{
        val INITIAL = DeviceVolumeState(maxVolume = -1, currentVolume = -1)
    }
}