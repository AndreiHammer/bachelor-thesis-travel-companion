package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import eu.ase.travelcompanionapp.app.navigation.routes.DestinationRoute
import eu.ase.travelcompanionapp.app.navigation.sharedKoinViewModel
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.recommendation.data.destinationapi.toDestination
import eu.ase.travelcompanionapp.recommendation.presentation.destinations.DestinationDetailScreen
import eu.ase.travelcompanionapp.recommendation.presentation.destinations.DestinationViewModel
import eu.ase.travelcompanionapp.recommendation.presentation.destinations.DestinationsListScreen
import eu.ase.travelcompanionapp.recommendation.presentation.main.RecommendationScreen
import eu.ase.travelcompanionapp.recommendation.presentation.main.RecommendationViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.DestinationGraph(navController: NavHostController) {
    navigation<DestinationRoute.DestinationGraph>(
        startDestination = DestinationRoute.Recommendations
    ) {
        composable<DestinationRoute.Recommendations>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            val recommendationViewModel = backStackEntry.sharedKoinViewModel<RecommendationViewModel>(
                navController = navController,
                parameters = { parametersOf(navController) }
            )
            
            RecommendationScreen(
                navController = navController,
                viewModel = recommendationViewModel
            )
        }
        
        composable<DestinationRoute.DestinationList>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            val sharedViewModel = backStackEntry.sharedKoinViewModel<SharedViewModel>(navController)
            val destinationViewModel = backStackEntry.sharedKoinViewModel<DestinationViewModel>(
                navController = navController,
                parameters = { parametersOf(navController, sharedViewModel) }
            )

            val recommendationViewModel = backStackEntry.sharedKoinViewModel<RecommendationViewModel>(
                navController = navController,
                parameters = { parametersOf(navController) }
            )
            val recommendationState by recommendationViewModel.state.collectAsStateWithLifecycle()
            
            DestinationsListScreen(
                navController = navController,
                viewModel = destinationViewModel,
                initialRecommendations = recommendationState.recommendations
            )
        }
        
        composable<DestinationRoute.DestinationDetail>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            val sharedViewModel = backStackEntry.sharedKoinViewModel<SharedViewModel>(navController)
            val destinationViewModel = backStackEntry.sharedKoinViewModel<DestinationViewModel>(
                navController = navController,
                parameters = { parametersOf(navController, sharedViewModel) }
            )
            val destination = backStackEntry.toRoute<DestinationRoute.DestinationDetail>().toDestination()
            
            DestinationDetailScreen(
                navController = navController,
                destination = destination,
                viewModel = destinationViewModel
            )
        }
    }
} 
