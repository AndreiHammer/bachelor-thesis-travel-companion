package eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.network

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.TouristAttractionDto

interface RemoteTouristAttractionsDataSource {
    suspend fun searchTouristAttractionsByLocation(
        latitude: Double,
        longitude: Double,
        onResult: (Result<List<TouristAttractionDto>, DataError.Remote>) -> Unit
    )
}