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
import eu.ase.travelcompanionapp.app.navigation.sharedKoinViewModel
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.test.HotelLocationTestScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.test.HotelLocationTestViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.HotelFavouriteScreen
import eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.HotelFavouriteViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListScreenRoot
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.HotelOffersScreen
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.HotelOffersViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchScreen
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.MapSearchScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.HotelGraph(navController: NavHostController) {
    navigation<HotelRoute.HotelGraph>(
        startDestination = HotelRoute.LocationSearch
    ){
        composable<HotelRoute.LocationSearch>(
            enterTransition = { slideInHorizontally() },
            exitTransition = { slideOutHorizontally() }
        ) {
            val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
            val viewModel = koinViewModel<LocationSearchViewModel>(parameters = { parametersOf(navController, sharedViewModel) })

            LocationSearchScreen(
                sharedViewModel = sharedViewModel,
                onAction = { action ->
                    viewModel.handleAction(action)
                }
            )
        }

        composable<HotelRoute.MapSearch>(
            enterTransition = { slideInHorizontally() },
            exitTransition = { slideOutHorizontally() }
        ) {
            val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
            val viewModel = koinViewModel<LocationSearchViewModel>(parameters = { parametersOf(navController, sharedViewModel) })

            MapSearchScreen(
                onAction = { action ->
                    viewModel.handleAction(action)
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
            val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
            val viewModel = koinViewModel<HotelListViewModel>(parameters = { parametersOf(navController, sharedViewModel) })

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
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                amenities = selectedAmenities,
                ratings = selectedRatings
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
            val viewModel = koinViewModel<HotelListViewModel>(parameters = { parametersOf(navController, sharedViewModel) })

            val selectedCity by sharedViewModel.selectedCity.collectAsStateWithLifecycle()
            val selectedRatings by sharedViewModel.selectedRatings.collectAsStateWithLifecycle()
            val selectedAmenities by sharedViewModel.selectedAmenities.collectAsStateWithLifecycle()

            LaunchedEffect(true) {
                sharedViewModel.onSelectHotel(null)
            }

            HotelListScreenRoot(
                viewModel = viewModel,
                selectedCity = selectedCity,
                amenities = selectedAmenities,
                ratings = selectedRatings
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
            val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
            val selectedHotel by sharedViewModel.selectedHotel.collectAsStateWithLifecycle()
            val checkInDate by sharedViewModel.selectedCheckInDate.collectAsStateWithLifecycle()
            val checkOutDate by sharedViewModel.selectedCheckOutDate.collectAsStateWithLifecycle()
            val adults by sharedViewModel.selectedAdults.collectAsStateWithLifecycle()

            selectedHotel?.let { selected ->
                /*val viewModel = koinViewModel<HotelLocationViewModel>(
                    parameters = { parametersOf(navController, sharedViewModel) }
                )

                HotelLocationScreenRoot(
                    hotel = selected,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    adults = adults,
                    viewModel = viewModel
                )*/

                val viewModel = koinViewModel<HotelLocationTestViewModel>(
                    parameters = { parametersOf(navController, sharedViewModel) }
                )

                HotelLocationTestScreenRoot(
                    hotel = selected,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    adults = adults,
                    viewModel = viewModel
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
            val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
            val selectedHotel by sharedViewModel.selectedHotel.collectAsStateWithLifecycle()
            val checkInDate by sharedViewModel.selectedCheckInDate.collectAsStateWithLifecycle()
            val checkOutDate by sharedViewModel.selectedCheckOutDate.collectAsStateWithLifecycle()
            val adults by sharedViewModel.selectedAdults.collectAsStateWithLifecycle()

            selectedHotel?.let{ hotel->
                val viewModel = koinViewModel<HotelOffersViewModel>(
                    parameters = { parametersOf(navController) }
                )

                HotelOffersScreen(
                    hotelId = hotel.hotelId,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    adults = adults,
                    viewModel = viewModel
                )
            }
        }

        composable<HotelRoute.Favourites>(
            enterTransition = { slideInHorizontally{initialOffset ->
                initialOffset
            } },
            exitTransition = { slideOutHorizontally{initialOffset ->
                initialOffset
            } }
        ) {
            val sharedViewModel = it.sharedKoinViewModel<SharedViewModel>(navController)
            val viewModel = koinViewModel<HotelFavouriteViewModel>(
                parameters = { parametersOf(navController, sharedViewModel)}
            )

            HotelFavouriteScreen(
                viewModel = viewModel
            )
        }
    }
}