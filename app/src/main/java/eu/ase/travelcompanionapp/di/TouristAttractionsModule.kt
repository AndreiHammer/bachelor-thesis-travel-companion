package eu.ase.travelcompanionapp.di

import androidx.navigation.NavController
import eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.network.TouristAttractionsAmadeusApiService
import eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.network.RemoteTouristAttractionsDataSource
import eu.ase.travelcompanionapp.touristattractions.data.amadeusApi.repository.AmadeusTouristAttractionRepository
import eu.ase.travelcompanionapp.touristattractions.domain.repository.TouristAttractionRepositoryAmadeusApi
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristSharedViewModel
import eu.ase.travelcompanionapp.touristattractions.presentation.TouristAttractionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val touristAttractionsModule = module {
    single<RemoteTouristAttractionsDataSource> { TouristAttractionsAmadeusApiService(get()) }
    single<TouristAttractionRepositoryAmadeusApi> { AmadeusTouristAttractionRepository(get()) }
    single { TouristSharedViewModel() }
    viewModel { (touristAttractionRepository: TouristAttractionRepositoryAmadeusApi, navController: NavController, sharedViewModel: TouristSharedViewModel) ->
        TouristAttractionsViewModel(
            touristAttractionRepository = touristAttractionRepository,
            navController = navController,
            sharedViewModel = sharedViewModel,
            priceConverter = get()
        )
    }
}