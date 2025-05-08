package eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.repository

import eu.ase.travelcompanionapp.core.domain.resulthandlers.DataError
import eu.ase.travelcompanionapp.core.domain.resulthandlers.Result
import eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.network.RemoteTouristAttractionsDataSource
import eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.toTouristAttraction
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction
import eu.ase.travelcompanionapp.touristattractions.domain.repository.TouristAttractionRepositoryAmadeusApi

class AmadeusTouristAttractionRepository(
    private val remotePOIDataSource: RemoteTouristAttractionsDataSource
) : TouristAttractionRepositoryAmadeusApi{
    override suspend fun searchTouristAttractionsByLocation(
        latitude: Double,
        longitude: Double,
        onResult: (Result<List<TouristAttraction>, DataError>) -> Unit
    ) {
        remotePOIDataSource.searchTouristAttractionsByLocation(latitude, longitude) { result->
            when(result) {
                is Result.Error -> onResult(Result.Error(result.error))
                is Result.Success -> {
                    onResult(Result.Success(result.data.map { it.toTouristAttraction() }))
                }
            }
        }
    }
}