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
import eu.ase.travelcompanionapp.authentication.presentation.login.LoginScreen
import eu.ase.travelcompanionapp.authentication.presentation.login.LoginViewModel
import eu.ase.travelcompanionapp.authentication.presentation.profile.ProfileAction
import eu.ase.travelcompanionapp.authentication.presentation.profile.ProfileScreen
import eu.ase.travelcompanionapp.authentication.presentation.signUp.SignUpScreen
import eu.ase.travelcompanionapp.authentication.presentation.signUp.SignUpViewModel
import eu.ase.travelcompanionapp.authentication.presentation.splash.SplashScreen
import eu.ase.travelcompanionapp.authentication.presentation.startScreen.StartScreen
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
        startDestination = Route.Root,
        modifier = modifier
    ) {
        navigation<Route.Root>(
            startDestination = Route.Splash
        ) {
            composable<Route.Splash>(
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } }
            ) {
                SplashScreen(
                    onUserKnown = {
                        navController.navigate(Route.HotelGraph) {
                            popUpTo(Route.Root) { inclusive = true }
                        }
                    },
                    onUserUnknown = {
                        navController.navigate(Route.AuthGraph) {
                            popUpTo(Route.Root) { inclusive = true }
                        }
                    }
                )
            }
        }

        navigation<Route.AuthGraph>(
            startDestination = Route.Start
        ) {
            composable<Route.Start>(
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } }
            ) {
                StartScreen(
                    onLoginClick = { navController.navigate(Route.Login) },
                    onSignUpClick = { navController.navigate(Route.SignUp) }
                )
            }

            composable<Route.SignUp>(
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } }
            ) {
                val viewModel = koinViewModel<SignUpViewModel>()
                SignUpScreen(
                    onSignUpClick = { _, _ ->
                        viewModel.onSignUpClick()
                        navController.navigate(Route.HotelGraph){
                            popUpTo(Route.AuthGraph) { inclusive = true }
                        }
                    }
                )
            }

            composable<Route.Login>(
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } }
            ) {
                val viewModel = koinViewModel<LoginViewModel>()
                LoginScreen(
                    onLoginClick = { _, _ ->
                        viewModel.onLoginClick()
                        navController.navigate(Route.HotelGraph){
                            popUpTo(Route.AuthGraph) { inclusive = true }
                        }
                    }
                )
            }
        }
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
                            LocationSearchAction.OnMapClick -> {
                                navController.navigate(Route.MapSearch)
                            }

                            is LocationSearchAction.OnLocationSelected -> {
                                locationSearchViewModel.setLocation(action.location, action.range, sharedViewModel)
                                navController.navigate(Route.HotelListLocation(action.location.latitude, action.location.longitude, action.range.toFloat()))
                            }

                            LocationSearchAction.OnProfileClick -> {
                                navController.navigate(Route.Profile)
                            }
                        }
                    }
                )
            }

           composable<Route.Profile>(
               enterTransition = { slideInHorizontally() },
               exitTransition = { slideOutHorizontally() }
           ) {
               ProfileScreen(
                   onAction = {
                       when(it){
                           ProfileAction.AccountDeleted -> {
                               navController.navigate(Route.AuthGraph){
                                   popUpTo(Route.HotelGraph) { inclusive = true }
                               }
                           }
                           ProfileAction.OnBackClick -> {
                               navController.popBackStack()
                           }
                           ProfileAction.SignedOut -> {
                               navController.navigate(Route.AuthGraph){
                                   popUpTo(Route.HotelGraph) { inclusive = true }
                               }
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

