package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch

import com.google.android.gms.maps.model.LatLng


sealed interface LocationSearchAction {
    data object OnNearbySearchClick: LocationSearchAction

    data object OnMapClick: LocationSearchAction

    data object OnBackClick: LocationSearchAction

    data class OnSearchClick(val city: String, val amenities: Set<String>, val rating: Set<Int>): LocationSearchAction

    data object OnProfileClick: LocationSearchAction

    data class OnRatingSelected(val ratings: Set<Int>): LocationSearchAction

    data class OnAmenitiesSelected(val amenities: Set<String>): LocationSearchAction

    data class OnCitySelected(val city: String): LocationSearchAction

    data class OnLocationSelected(val location: LatLng, val range: Int) : LocationSearchAction

    data class OnOfferDetailsSet(val checkInDate: String, val checkOutDate: String, val adults: Int) : LocationSearchAction
    
    data object OnRecommendationsClick: LocationSearchAction
}