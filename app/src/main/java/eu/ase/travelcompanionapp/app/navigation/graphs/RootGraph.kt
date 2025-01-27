package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.AuthRoute
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.app.navigation.routes.RootRoute
import eu.ase.travelcompanionapp.authentication.presentation.splash.SplashScreen

fun NavGraphBuilder.RootGraph(navController: NavHostController) {

    navigation<RootRoute.RootGraph>(
        startDestination = RootRoute.Splash
    ) {
        composable<RootRoute.Splash>(
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) {
            SplashScreen(
                onUserKnown = {
                    navController.navigate(HotelRoute.HotelGraph) {
                        popUpTo(RootRoute.RootGraph) { inclusive = true }
                    } },
                onUserUnknown = {
                    navController.navigate(AuthRoute.AuthGraph) {
                        popUpTo(RootRoute.RootGraph) { inclusive = true }
                    }
                }
            )
        }
    }
}

