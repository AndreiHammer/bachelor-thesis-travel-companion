package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.PaymentRoute
import eu.ase.travelcompanionapp.app.navigation.sharedKoinViewModel
import eu.ase.travelcompanionapp.booking.presentation.payment.PaymentScreen
import eu.ase.travelcompanionapp.booking.presentation.payment.PaymentViewModel
import eu.ase.travelcompanionapp.booking.presentation.bookinghistory.BookingHistoryScreen
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.PaymentGraph(navController: NavHostController) {
    navigation<PaymentRoute.PaymentGraph>(
        startDestination = PaymentRoute.Payment
    ) {
        composable<PaymentRoute.Payment>(
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) { 
            val viewModel = koinViewModel<PaymentViewModel>()
            
            PaymentScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        
        composable<PaymentRoute.BookingHistory>(
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) {
            val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
            
            BookingHistoryScreen(
                navController = navController,
                viewModel = koinViewModel { parametersOf(navController, sharedViewModel) }
            )
        }
    }
}