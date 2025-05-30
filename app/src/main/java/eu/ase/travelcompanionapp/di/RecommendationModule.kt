package eu.ase.travelcompanionapp.di

import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.recommendation.data.RecommendationRepositoryImpl
import eu.ase.travelcompanionapp.recommendation.domain.RecommendationRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.recommendation.presentation.RecommendationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val recommendationModule = module {
    single<RecommendationRepository> { RecommendationRepositoryImpl() }

    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        RecommendationViewModel(
            navController = navController,
            recommendationRepository = get(),
            hotelThumbnailRepository = get(),
            sharedViewModel = sharedViewModel
        )
    }
} 