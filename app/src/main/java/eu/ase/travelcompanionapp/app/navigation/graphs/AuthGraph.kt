package eu.ase.travelcompanionapp.app.navigation.graphs

import android.content.Context
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.AuthRoute
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.authentication.presentation.login.LoginScreen
import eu.ase.travelcompanionapp.authentication.presentation.login.LoginViewModel
import eu.ase.travelcompanionapp.authentication.presentation.signUp.SignUpScreen
import eu.ase.travelcompanionapp.authentication.presentation.signUp.SignUpViewModel
import eu.ase.travelcompanionapp.authentication.presentation.startScreen.StartScreen
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.AuthGraph(navController: NavHostController, context: Context) {
    navigation<AuthRoute.AuthGraph>(
        startDestination = AuthRoute.Start
    ) {
        composable<AuthRoute.Start>(
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) {
            StartScreen(
                onLoginClick = { navController.navigate(AuthRoute.Login) },
                onSignUpClick = { navController.navigate(AuthRoute.SignUp) }
            )
        }

        composable<AuthRoute.SignUp>(
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) {
            val viewModel = koinViewModel<SignUpViewModel>()
            SignUpScreen(
                onSignUpClick = { _, _ ->
                    viewModel.onSignUpClick(context)
                    navController.navigate(HotelRoute.HotelGraph){
                        popUpTo(AuthRoute.AuthGraph) { inclusive = true }
                    }
                }
            )
        }

        composable<AuthRoute.Login>(
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) {
            val viewModel = koinViewModel<LoginViewModel>()
            LoginScreen(
                onLoginClick = { _, _ ->
                    viewModel.onLoginClick(context)
                    navController.navigate(HotelRoute.HotelGraph){
                        popUpTo(AuthRoute.AuthGraph) { inclusive = true }
                    }
                }
            )
        }
    }
}