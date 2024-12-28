package eu.ase.travelcompanionapp.app

import android.util.Log
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
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchAction
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchScreen
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchViewModel
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.components.MapSearchScreen
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
                                sharedViewModel.onSelectCity(action.city)
                                navController.navigate(Route.HotelListCity(city = action.city))
                            }
                            LocationSearchAction.onMapClick -> {
                                navController.navigate(Route.MapSearch)
                            }

                            is LocationSearchAction.OnLocationSelected -> {
                                locationSearchViewModel.setLocation(action.location, action.range, sharedViewModel)
                                navController.navigate(Route.HotelListLocation(action.location.latitude, action.location.longitude, action.range.toFloat()))
                            }
                        }
                    }
                )
            }

            composable<Route.MapSearch>(
                enterTransition = { slideInHorizontally() },
                exitTransition = { slideOutHorizontally() }
            ) {

                val locationSearchViewModel = koinViewModel<LocationSearchViewModel>()
                val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
                MapSearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onLocationSelected = { location, range ->
                        locationSearchViewModel.setLocation(location, range, sharedViewModel)
                        navController.navigate(Route.HotelListLocation(location.latitude, location.longitude, range.toFloat()))
                    },
                    modifier = Modifier
                )
            }

            composable<Route.HotelListLocation>(
                enterTransition = { slideInHorizontally{initialOffset ->
                    initialOffset
                } },
                exitTransition = { slideOutHorizontally { initialOffset ->
                    initialOffset
                }}
            ){
                val viewModel = koinViewModel<HotelListViewModel>()
                val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
                val locationState = sharedViewModel.selectedLocation.collectAsStateWithLifecycle()

                val latitude = locationState.value.location?.latitude
                val longitude = locationState.value.location?.longitude
                val radius = locationState.value.range
                Log.d("Location", "Latitude: $latitude, Longitude: $longitude, Radius: $radius")

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
                    latitude = latitude,
                    longitude = longitude,
                    radius = radius,
                    onBackClick = { navController.popBackStack() }
                )


            }

            composable<Route.HotelListCity>(
                enterTransition = { slideInHorizontally{initialOffset ->
                    initialOffset
                } },
                exitTransition = { slideOutHorizontally{initialOffset ->
                    initialOffset
                } }
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