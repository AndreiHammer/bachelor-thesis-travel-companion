package eu.ase.travelcompanionapp.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import eu.ase.travelcompanionapp.hotel.presentation.SelectedHotelViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.location.HotelLocationScreenRoot
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.HotelGraph,
        modifier = modifier
    ) {
        navigation<Route.HotelGraph>(
            startDestination = Route.HotelList
        ){
            composable<Route.HotelList>(
                exitTransition = { slideOutHorizontally() },
                popEnterTransition = {
                    slideInHorizontally()
                }
            ){
                val selectedHotelViewModel = it.sharedKoinViewModel<SelectedHotelViewModel>(navController)
                val viewModel = koinViewModel<HotelListViewModel>()
                LaunchedEffect(true) {
                    selectedHotelViewModel.onSelectHotel(null)
                }

                HotelListScreenRoot(
                    viewModel = viewModel,
                    onHotelClick = {hotel->
                        selectedHotelViewModel.onSelectHotel(hotel)
                        navController.navigate(
                            Route.HotelDetail(hotel.hotelId)
                        )

                    }
                )
            }

            composable<Route.HotelDetail>(
                enterTransition = { slideInHorizontally{initialOffset ->
                    initialOffset
                } },
                exitTransition = { slideOutHorizontally{initialOffset ->
                    initialOffset
                } }
            ) {
                val selectedHotelViewModel =
                    it.sharedKoinViewModel<SelectedHotelViewModel>(navController)
                val selectedHotel by selectedHotelViewModel.selectedHotel.collectAsStateWithLifecycle()

                selectedHotel?.let { selected ->
                    HotelLocationScreenRoot(
                        hotel = selected,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }


    }
}


@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}