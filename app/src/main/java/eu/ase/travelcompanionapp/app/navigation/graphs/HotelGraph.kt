package eu.ase.travelcompanionapp.app.navigation.graphs

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.app.navigation.routes.ProfileRoute
import eu.ase.travelcompanionapp.app.navigation.sharedKoinViewModel
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.HotelOffersAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.HotelOffersScreen
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
            val sharedViewModel =
                it.sharedKoinViewModel<SharedViewModel>(navController)

            LocationSearchScreen(
                onAction = { action ->
                    when (action) {
                        is LocationSearchAction.OnSearchClick -> {
                            sharedViewModel.onSelectCity(action.city)
                            navController.navigate(
                                HotelRoute.HotelListCity(
                                    city = action.city
                                )
                            )
                        }

                        LocationSearchAction.OnMapClick -> {
                            navController.navigate(HotelRoute.MapSearch)
                        }

                        LocationSearchAction.OnProfileClick -> {
                            navController.navigate(ProfileRoute.Profile)
                        }

                        is LocationSearchAction.OnAmenitiesSelected -> {
                            sharedViewModel.onSelectAmenities(action.amenities)
                        }
                        is LocationSearchAction.OnRatingSelected -> {
                            sharedViewModel.onSelectRating(action.ratings)
                        }
                        is LocationSearchAction.OnCitySelected -> {
                            sharedViewModel.onSelectCity(action.city)
                        }
                        is LocationSearchAction.OnLocationSelected -> {}
                        is LocationSearchAction.OnOfferDetailsSet -> {
                            sharedViewModel.onSelectDates(action.checkInDate, action.checkOutDate)
                            sharedViewModel.onSelectAdults(action.adults)
                        }

                        LocationSearchAction.OnBackClick -> {}
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
                onAction = { action ->
                    when (action) {
                        is LocationSearchAction.OnAmenitiesSelected -> {
                            sharedViewModel.onSelectAmenities(action.amenities)
                        }
                        is LocationSearchAction.OnRatingSelected -> {
                            sharedViewModel.onSelectRating(action.ratings)
                        }
                        is LocationSearchAction.OnCitySelected -> {
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
                        is LocationSearchAction.OnOfferDetailsSet -> {
                            sharedViewModel.onSelectDates(action.checkInDate, action.checkOutDate)
                            sharedViewModel.onSelectAdults(action.adults)
                        }

                        LocationSearchAction.OnBackClick -> {
                            navController.popBackStack()
                        }
                        LocationSearchAction.OnMapClick -> TODO()
                        LocationSearchAction.OnProfileClick -> TODO()
                        is LocationSearchAction.OnSearchClick -> TODO()
                    }
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
            val checkInDate by sharedViewModel.selectedCheckInDate.collectAsStateWithLifecycle()
            val checkOutDate by sharedViewModel.selectedCheckOutDate.collectAsStateWithLifecycle()
            val adults by sharedViewModel.selectedAdults.collectAsStateWithLifecycle()

            selectedHotel?.let { selected ->
                HotelLocationScreenRoot(
                    hotel = selected,
                    onAction = { action ->
                        when(action){
                            HotelLocationAction.OnBackClick -> {
                                navController.popBackStack()
                            }
                            is HotelLocationAction.OnViewOfferClick -> {
                                navController.navigate(
                                    HotelRoute.HotelOffers(
                                        hotelId = selected.hotelId,
                                        checkInDate = checkInDate,
                                        checkOutDate = checkOutDate,
                                        adults = adults
                                    )
                                )
                            }
                        }
                    },
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    adults = adults
                )
            }
        }

        composable<HotelRoute.HotelOffers>(
            enterTransition = { slideInHorizontally{initialOffset ->
                initialOffset
            } },
            exitTransition = { slideOutHorizontally{initialOffset ->
                initialOffset
            } }
        ){
            val sharedViewModel =
                it.sharedKoinViewModel<SharedViewModel>(navController)
            val selectedHotel by sharedViewModel.selectedHotel.collectAsStateWithLifecycle()
            val checkInDate by sharedViewModel.selectedCheckInDate.collectAsStateWithLifecycle()
            val checkOutDate by sharedViewModel.selectedCheckOutDate.collectAsStateWithLifecycle()
            val adults by sharedViewModel.selectedAdults.collectAsStateWithLifecycle()

            selectedHotel?.let{ hotel->
                HotelOffersScreen(
                    hotelId = hotel.hotelId,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    adults = adults,
                    onAction = { action ->
                        when(action) {
                            HotelOffersAction.OnBackClick -> {
                                navController.popBackStack()
                            }
                            HotelOffersAction.OnBookNow -> {}
                        }
                    }
                )
            }
        }
    }
}