package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.PaymentRoute
import eu.ase.travelcompanionapp.payment.presentation.PaymentScreen
import eu.ase.travelcompanionapp.payment.presentation.PaymentViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.PaymentGraph(navController: NavHostController) {
    navigation(
        route = PaymentRoute.PaymentGraph.toString(),
        startDestination = PaymentRoute.Payment.ROUTE
    ) {
        composable(
            route = PaymentRoute.Payment.ROUTE,
            arguments = PaymentRoute.Payment.paymentArguments,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry ->
            val viewModel = koinViewModel<PaymentViewModel>()

            val bookingReference = backStackEntry.arguments?.getString("bookingReference") ?: ""

            LaunchedEffect(key1 = Unit) {
                viewModel.loadBookingDetails(bookingReference)
            }

            PaymentScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}