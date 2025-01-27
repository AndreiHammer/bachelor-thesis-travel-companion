package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.AuthRoute
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.app.navigation.routes.ProfileRoute
import eu.ase.travelcompanionapp.authentication.presentation.profile.ProfileAction
import eu.ase.travelcompanionapp.authentication.presentation.profile.ProfileScreen

fun NavGraphBuilder.ProfileGraph(navController: NavHostController) {
    navigation<ProfileRoute.ProfileGraph>(
        startDestination = ProfileRoute.Profile
    ){
        composable<ProfileRoute.Profile>(
            enterTransition = { slideInHorizontally() },
            exitTransition = { slideOutHorizontally() }
        ) {
            ProfileScreen(
                onAction = {
                    when(it){
                        ProfileAction.AccountDeleted -> {
                            navController.navigate(AuthRoute.AuthGraph){
                                popUpTo(HotelRoute.HotelGraph) { inclusive = true }
                            }
                        }
                        ProfileAction.OnBackClick -> {
                            navController.popBackStack()
                        }
                        ProfileAction.SignedOut -> {
                            navController.navigate(AuthRoute.AuthGraph){
                                popUpTo(HotelRoute.HotelGraph) { inclusive = true }
                            }
                        }
                    }
                }

            )
        }
    }
}