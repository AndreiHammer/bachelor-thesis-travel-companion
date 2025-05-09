package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.TouristAttractionsRoute
import eu.ase.travelcompanionapp.app.navigation.sharedKoinViewModel
import eu.ase.travelcompanionapp.touristattractions.domain.repository.TouristAttractionRepositoryAmadeusApi
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristSharedViewModel
import eu.ase.travelcompanionapp.touristattractions.presentation.details.TouristAttractionDetailsScreen
import eu.ase.travelcompanionapp.touristattractions.presentation.list.TouristAttractionsListScreen
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
        ) {
            val sharedViewModel = it.sharedKoinViewModel<TouristSharedViewModel>(navController)
            val touristAttractionRepository = koinInject<TouristAttractionRepositoryAmadeusApi>()
            val viewModel = koinViewModel<TouristAttractionsViewModel> { 
                parametersOf(touristAttractionRepository, navController, sharedViewModel) 
            }
            
            TouristAttractionsListScreen(
                viewModel = viewModel,
                sharedViewModel = sharedViewModel
            )
        }
        
        composable<TouristAttractionsRoute.TouristAttractionDetails>(
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { it } }
        ) { backStackEntry ->
            val sharedViewModel = backStackEntry.sharedKoinViewModel<TouristSharedViewModel>(navController)
            val touristAttractionRepository = koinInject<TouristAttractionRepositoryAmadeusApi>()
            val viewModel = koinViewModel<TouristAttractionsViewModel> { 
                parametersOf(touristAttractionRepository, navController, sharedViewModel) 
            }
            
            TouristAttractionDetailsScreen(
                viewModel = viewModel,
                sharedViewModel = sharedViewModel
            )
        }
    }
} 