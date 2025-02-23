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
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchScreen
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.MapSearchScreen
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
                            sharedViewModel.onSelectAmenities(action.amenities)
                            sharedViewModel.onSelectRating(action.rating)
                            navController.navigate(
                                HotelRoute.HotelListCity(
                                    city = action.city,
                                    amenities = action.amenities.joinToString(","),
                                    rating = action.rating.joinToString(",")
                                )
                            )
                        }

                        LocationSearchAction.OnMapClick -> {
                            navController.navigate(HotelRoute.MapSearch)
                        }

                        is LocationSearchAction.OnLocationSelected -> {
                            locationSearchViewModel.setLocation(
                                action.location,
                                action.range,
                                sharedViewModel
                            )
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
                },
                onRatingSelected = { ratings ->
                    sharedViewModel.onSelectRating(ratings)
                },
                onAmenitiesSelected = { amenities ->
                    sharedViewModel.onSelectAmenities(amenities)
                },
                onCitySelected = { city ->
                    sharedViewModel.onSelectCity(city)
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
                modifier = Modifier,
                onRatingSelected = { ratings ->
                    sharedViewModel.onSelectRating(ratings)
                },
                onAmenitiesSelected = { amenities ->
                    sharedViewModel.onSelectAmenities(amenities)
                }
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
            val selectedRatings by sharedViewModel.selectedRatings.collectAsStateWithLifecycle()
            val selectedAmenities by sharedViewModel.selectedAmenities.collectAsStateWithLifecycle()

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
                amenities = selectedAmenities,
                ratings = selectedRatings,
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
            val selectedRatings by sharedViewModel.selectedRatings.collectAsStateWithLifecycle()
            val selectedAmenities by sharedViewModel.selectedAmenities.collectAsStateWithLifecycle()

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
                amenities = selectedAmenities,
                ratings = selectedRatings,
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