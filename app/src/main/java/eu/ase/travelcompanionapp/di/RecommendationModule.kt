package eu.ase.travelcompanionapp.di

import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.hotel.data.recommendation.RecommendationRepositoryImpl
import eu.ase.travelcompanionapp.hotel.domain.repository.RecommendationRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.recommendations.RecommendationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val recommendationModule = module {
    single<RecommendationRepository> { 
        RecommendationRepositoryImpl(
            favouriteHotelRepository = get(),
        ) 
    }

    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        RecommendationViewModel(
            navController = navController,
            recommendationRepository = get(),
            hotelThumbnailRepository = get(),
            sharedViewModel = sharedViewModel
        )
    }
} 