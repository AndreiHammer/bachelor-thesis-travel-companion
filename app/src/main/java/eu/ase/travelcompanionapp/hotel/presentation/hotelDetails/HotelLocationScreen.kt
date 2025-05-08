package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.hotel.domain.model.Hotel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components.HotelDetails
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HotelLocationScreenRoot(
    hotel: Hotel,
    modifier: Modifier = Modifier,
    checkInDate: String,
    checkOutDate: String,
    adults: Int,
    viewModel: HotelLocationViewModel = koinViewModel(),
    navController: NavHostController? = null
) {
    HotelDetails(
        hotel = hotel,
        modifier = modifier,
        viewModel = viewModel,
        navController = navController ?: koinInject(),
        checkInDate = checkInDate,
        checkOutDate = checkOutDate,
        adults = adults
    )
}



