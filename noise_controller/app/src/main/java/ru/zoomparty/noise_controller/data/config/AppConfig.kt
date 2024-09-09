package ru.zoomparty.noise_controller.data.config

import ru.zoomparty.noise_controller.App
import java.io.File

val temp_file_record ="${App.appContext.cacheDir.absolutePath}${File.pathSeparator}temp_noise_controller.wav"
var TIME_DELAY = 100L