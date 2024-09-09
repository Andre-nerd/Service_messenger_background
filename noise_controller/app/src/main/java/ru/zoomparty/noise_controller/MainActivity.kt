package ru.zoomparty.noise_controller

import android.Manifest.permission.FOREGROUND_SERVICE
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.zoomparty.noise_controller.ui.compose.navigation.AppNavHost
import ru.zoomparty.noise_controller.ui.theme.Audio_controllerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ru.zoomparty.noise_controller.data.NoiseController.Companion.NOISE_IN_MIC
import ru.zoomparty.noise_controller.data.SoundService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Audio_controllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)){
                        FeatureThatRequiresPermissions()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i(NOISE_IN_MIC, "Main Activity onStop()")
    }

    override fun onPause() {
        super.onPause()
        Log.i(NOISE_IN_MIC, "Main Activity onPause()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(NOISE_IN_MIC, "Main Activity onDestroy()")
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun FeatureThatRequiresPermissions() {
        val permissionsState = rememberMultiplePermissionsState(
            if(Build.VERSION.SDK_INT >= 33) {
                listOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    FOREGROUND_SERVICE,
                    POST_NOTIFICATIONS
                )
            } else{
                listOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    FOREGROUND_SERVICE,
                )
            }
        )
        if (permissionsState.allPermissionsGranted) {
            SoundService.startService(App.appContext,"service started")
            AppNavHost()
        } else {
            ShowScreenRationalePermission(permissionsState)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun ShowScreenRationalePermission(permissionsState: MultiplePermissionsState){
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.padding(16.dp)) {

                Spacer(modifier = Modifier.size(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Text("Для корректной работы приложения необходимо предоставить пользовательские разрешения.")
                }
                Spacer(modifier = Modifier.size(40.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Button(onClick = {
                        permissionsState.launchMultiplePermissionRequest()
                    }) {
                        Text("Предоставить разрешения")
                    }
                }
            }
        }
    }
}



