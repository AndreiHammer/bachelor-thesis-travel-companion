package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.app.navigation.routes.ProfileRoute
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import eu.ase.travelcompanionapp.core.domain.utils.LocationUtils
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationSearchViewModel(
    private val cityToIATACodeRepository: CityToIATACodeRepository,
    private val navController: NavHostController,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {

    private val dateUtils = DateUtils()
    private val locationUtils = LocationUtils()

    private val _citiesWithCountry = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val citiesWithCountry: StateFlow<List<Pair<String, String>>> = _citiesWithCountry

    private val _locationState = MutableStateFlow(LocationState())

    private fun setLocation(location: LatLng, range: Int) {
        _locationState.value = LocationState(location, range)
        sharedViewModel.onSelectLocation(location, range)
    }

    data class LocationState(
        val location: LatLng? = null,
        val range: Int = 0
    )

    fun fetchSuggestions() {
        _citiesWithCountry.value = cityToIATACodeRepository.getCityWithCountrySuggestions()
    }

    fun performNearbySearch(
        context: Context,
        sharedViewModel: SharedViewModel,
        radius: Int,
        onResult: (Boolean) -> Unit
    ) {
        val (currentDate, nextDay) = dateUtils.getCurrentAndNextDayDates()

        sharedViewModel.onSelectDates(currentDate, nextDay)
        sharedViewModel.onSelectAdults(2)

        locationUtils.getUserLocation(context) { location ->
            if (location != null) {
                val latLng = locationUtils.locationToLatLng(location)
                sharedViewModel.onSelectLocation(latLng, radius)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }


    fun handleAction(action: LocationSearchAction) {
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

            is LocationSearchAction.OnLocationSelected -> {
                setLocation(action.location, action.range)
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


            LocationSearchAction.OnNearbySearchClick -> {
                val locationState = sharedViewModel.selectedLocation.value
                if (locationState.location != null) {
                    navController.navigate(
                        HotelRoute.HotelListLocation(
                            locationState.location.latitude,
                            locationState.location.longitude,
                            locationState.range.toFloat()
                        )
                    )
                }
            }

            LocationSearchAction.OnBackClick -> {
                navController.popBackStack()
            }
            
            LocationSearchAction.OnRecommendationsClick -> {
                navController.navigate(HotelRoute.Recommendations)
            }
        }
    }
}