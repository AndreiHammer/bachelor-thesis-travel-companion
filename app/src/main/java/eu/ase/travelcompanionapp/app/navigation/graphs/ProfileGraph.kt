package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.ProfileRoute
import eu.ase.travelcompanionapp.authentication.presentation.profile.ProfileScreen
import eu.ase.travelcompanionapp.authentication.presentation.profile.ProfileViewModel
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
    }
}