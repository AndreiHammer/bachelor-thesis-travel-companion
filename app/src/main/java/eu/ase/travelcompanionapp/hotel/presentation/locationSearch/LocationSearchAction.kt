package eu.ase.travelcompanionapp.hotel.presentation.locationSearch

import com.google.android.gms.maps.model.LatLng

sealed interface LocationSearchAction {
    data object OnMapClick: LocationSearchAction

    data class OnSearchClick(val city: String): LocationSearchAction

    data class OnLocationSelected(val location: LatLng, val range: Int) : LocationSearchAction
}