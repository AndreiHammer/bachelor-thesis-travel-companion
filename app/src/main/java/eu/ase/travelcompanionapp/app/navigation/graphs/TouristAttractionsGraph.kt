package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.TouristAttractionsRoute
import eu.ase.travelcompanionapp.touristattractions.domain.repository.TouristAttractionRepositoryAmadeusApi
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristAttractionDetailsScreen
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristAttractionsListScreen
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristAttractionsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.TouristAttractionsGraph(navController: NavHostController) {
    navigation<TouristAttractionsRoute.TouristAttractionsGraph>(
        startDestination = TouristAttractionsRoute.TouristAttractionsList::class
    ) {
        composable<TouristAttractionsRoute.TouristAttractionsList>(
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getDouble("latitude") ?: 0.0
            val longitude = backStackEntry.arguments?.getDouble("longitude") ?: 0.0
            
            val touristAttractionRepository = koinInject<TouristAttractionRepositoryAmadeusApi>()
            val viewModel = koinViewModel<TouristAttractionsViewModel> { 
                parametersOf(touristAttractionRepository, navController) 
            }
            
            TouristAttractionsListScreen(
                viewModel = viewModel,
                latitude = latitude,
                longitude = longitude
            )
        }
        
        composable<TouristAttractionsRoute.TouristAttractionDetails>(
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry ->
            val attractionId = backStackEntry.arguments?.getString("attractionId") ?: ""
            
            val touristAttractionRepository = koinInject<TouristAttractionRepositoryAmadeusApi>()
            val viewModel = koinViewModel<TouristAttractionsViewModel> { 
                parametersOf(touristAttractionRepository, navController) 
            }
            
            TouristAttractionDetailsScreen(
                viewModel = viewModel,
                attractionId = attractionId
            )
        }
    }
} 