package ru.zoomparty.noise_controller.domain.state

data class DeviceMicState(
    val noiseLevel:Int
) {
    companion object{
        val INITIAL = DeviceMicState( noiseLevel =  -1)
    }
}
