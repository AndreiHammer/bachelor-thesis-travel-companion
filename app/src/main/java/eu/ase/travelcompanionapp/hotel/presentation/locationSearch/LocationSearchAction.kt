package eu.ase.travelcompanionapp.hotel.presentation.locationSearch

import com.google.android.gms.maps.model.LatLng

sealed interface LocationSearchAction {
    data object OnMapClick: LocationSearchAction

    data class OnSearchClick(val city: String, val amenities: Set<String>, val rating: Set<Int>): LocationSearchAction

    data object OnProfileClick: LocationSearchAction

    data class OnLocationSelected(val location: LatLng, val range: Int) : LocationSearchAction
}