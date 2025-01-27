package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.app.navigation.routes.ProfileRoute
import eu.ase.travelcompanionapp.app.navigation.sharedKoinViewModel
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchAction
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchScreen
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchViewModel
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.components.MapSearchScreen
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.HotelGraph(navController: NavHostController) {
    navigation<HotelRoute.HotelGraph>(
        startDestination = HotelRoute.LocationSearch
    ){
        composable<HotelRoute.LocationSearch>(
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
                            navController.navigate(HotelRoute.HotelListCity(city = action.city))
                        }
                        LocationSearchAction.OnMapClick -> {
                            navController.navigate(HotelRoute.MapSearch)
                        }

                        is LocationSearchAction.OnLocationSelected -> {
                            locationSearchViewModel.setLocation(action.location, action.range, sharedViewModel)
                            navController.navigate(
                                HotelRoute.HotelListLocation(
                                    action.location.latitude,
                                    action.location.longitude,
                                    action.range.toFloat()
                                )
                            )
                        }

                        LocationSearchAction.OnProfileClick -> {
                            navController.navigate(ProfileRoute.Profile)
                        }
                    }
                }
            )
        }

        composable<HotelRoute.MapSearch>(
            enterTransition = { slideInHorizontally() },
            exitTransition = { slideOutHorizontally() }
        ) {

            val locationSearchViewModel = koinViewModel<LocationSearchViewModel>()
            val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
            MapSearchScreen(
                onBackClick = { navController.popBackStack() },
                onLocationSelected = { location, range ->
                    locationSearchViewModel.setLocation(location, range, sharedViewModel)
                    navController.navigate(
                        HotelRoute.HotelListLocation(
                            location.latitude,
                            location.longitude,
                            range.toFloat()
                        )
                    )
                },
                modifier = Modifier
            )
        }

        composable<HotelRoute.HotelListLocation>(
            enterTransition = { slideInHorizontally{initialOffset ->
                initialOffset
            } },
            exitTransition = { slideOutHorizontally { initialOffset ->
                initialOffset
            }
            }
        ){
            val viewModel = koinViewModel<HotelListViewModel>()
            val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
            val locationState = sharedViewModel.selectedLocation.collectAsStateWithLifecycle()

            val latitude = locationState.value.location?.latitude
            val longitude = locationState.value.location?.longitude
            val radius = locationState.value.range

            LaunchedEffect(true) {
                sharedViewModel.onSelectHotel(null)
            }

            HotelListScreenRoot(
                viewModel = viewModel,
                onHotelClick = { hotel ->
                    sharedViewModel.onSelectHotel(hotel)
                    navController.navigate(
                        HotelRoute.HotelDetail(hotel.hotelId)
                    )

                },
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<HotelRoute.HotelListCity>(
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
                        HotelRoute.HotelDetail(hotel.hotelId)
                    )

                },
                selectedCity = selectedCity,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<HotelRoute.HotelDetail>(
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