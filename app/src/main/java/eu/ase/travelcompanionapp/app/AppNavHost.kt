package eu.ase.travelcompanionapp.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchAction
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchScreen
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchViewModel
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
            startDestination = Route.LocationSearch
        ){
            composable<Route.LocationSearch>(
                enterTransition = { slideInHorizontally() },
                exitTransition = { slideOutHorizontally() }
            ) {
                val locationSearchViewModel = koinViewModel<LocationSearchViewModel>()
                val sharedViewModel =
                    it.sharedKoinViewModel<SharedViewModel>(navController)

                LaunchedEffect(true) {
                    sharedViewModel.onSelectCity("")
                }
                LocationSearchScreen(
                    onAction = { action ->
                        when (action) {
                            is LocationSearchAction.OnSearchClick -> {
                                // Navigate to HotelList, passing the selected city
                                sharedViewModel.onSelectCity(action.city)
                                navController.navigate(Route.HotelList(city = action.city))
                            }
                            LocationSearchAction.onMapClick -> {
                                // Navigate to the map screen
                            }
                        }
                    }
                )
            }

            composable<Route.HotelList>(
                exitTransition = { slideOutHorizontally() },
                popEnterTransition = {
                    slideInHorizontally()
                }
            ){

                val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
                val viewModel = koinViewModel<HotelListViewModel>()
                val selectedCity by sharedViewModel.selectedCity.collectAsStateWithLifecycle()

                LaunchedEffect(true) {
                    sharedViewModel.onSelectHotel(null)
                }

                HotelListScreenRoot(
                    viewModel = viewModel,
                    onHotelClick = { hotel ->
                        sharedViewModel.onSelectHotel(hotel)
                        navController.navigate(
                            Route.HotelDetail(hotel.hotelId)
                        )

                    },
                    selectedCity = selectedCity,
                    context = LocalContext.current,
                    onBackClick = { navController.popBackStack() }
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
                val sharedViewModel =
                    it.sharedKoinViewModel<SharedViewModel>(navController)
                val selectedHotel by sharedViewModel.selectedHotel.collectAsStateWithLifecycle()

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