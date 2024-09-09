package ru.zoomparty.noise_controller.ui.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.zoomparty.noise_controller.ui.compose.screens.VolumeScreen
import kotlinx.serialization.Serializable


@Serializable
object VolumeScreen
@Serializable
object SecondPoint



@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    ) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = VolumeScreen
    ) {
        composable<VolumeScreen> {
            VolumeScreen(
                onNavigateToMainScreen = { navController.navigate(route = SecondPoint) }
            )
        }
    }
}


