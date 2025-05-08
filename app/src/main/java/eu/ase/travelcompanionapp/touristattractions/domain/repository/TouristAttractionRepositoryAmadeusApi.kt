package eu.ase.travelcompanionapp.touristattractions.domain.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction

interface TouristAttractionRepositoryAmadeusApi {

    suspend fun searchTouristAttractionsByLocation(
        latitude: Double,
        longitude: Double,
        onResult: (Result<List<TouristAttraction>, DataError>) -> Unit
    )
}