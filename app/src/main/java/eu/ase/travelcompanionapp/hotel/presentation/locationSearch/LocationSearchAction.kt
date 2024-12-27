package eu.ase.travelcompanionapp.hotel.presentation.locationSearch

sealed interface LocationSearchAction {
    data object onMapClick: LocationSearchAction

    data class OnSearchClick(val city: String): LocationSearchAction
}