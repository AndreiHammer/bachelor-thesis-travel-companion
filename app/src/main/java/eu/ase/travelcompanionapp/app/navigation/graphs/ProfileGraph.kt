package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.ProfileRoute
import eu.ase.travelcompanionapp.user.presentation.profile.ProfileScreen
import eu.ase.travelcompanionapp.user.presentation.profile.ProfileViewModel
import eu.ase.travelcompanionapp.user.presentation.settings.SettingsScreen
import eu.ase.travelcompanionapp.user.presentation.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.ProfileGraph(navController: NavHostController) {
    navigation<ProfileRoute.ProfileGraph>(
        startDestination = ProfileRoute.Profile
    ){
        composable<ProfileRoute.Profile>(
            enterTransition = { slideInHorizontally() },
            exitTransition = { slideOutHorizontally() }
        ) {
            val viewModel = koinViewModel<ProfileViewModel>(
                parameters = { parametersOf(navController) }
            )

            ProfileScreen(viewModel = viewModel)
        }

        composable<ProfileRoute.Settings>(
            enterTransition = { slideInHorizontally() },
            exitTransition = { slideOutHorizontally() }
        ) {
            val viewModel = koinViewModel<SettingsViewModel>(
                parameters = { parametersOf(navController) }
            )
            SettingsScreen(
                viewModel = viewModel
            )
        }
    }
}