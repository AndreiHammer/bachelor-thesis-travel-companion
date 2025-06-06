package eu.ase.travelcompanionapp.di

import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.recommendation.data.destinationapi.UserProfileService
import eu.ase.travelcompanionapp.recommendation.data.destinationapi.network.DestinationsRecommenderApiService
import eu.ase.travelcompanionapp.recommendation.data.travelpreferences.RecommendationUserPreferences
import eu.ase.travelcompanionapp.recommendation.domain.repository.DestinationApiRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserPreferencesRepository
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserProfileRepository
import eu.ase.travelcompanionapp.recommendation.presentation.destinations.DestinationViewModel
import eu.ase.travelcompanionapp.recommendation.presentation.main.RecommendationViewModel
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.QuestionnaireViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val recommendationModule = module {
    single<UserPreferencesRepository> { RecommendationUserPreferences(get()) }

    single<DestinationApiRepository> { DestinationsRecommenderApiService(get()) }

    single<UserProfileRepository> { 
        UserProfileService(
            userPreferencesRepository = get(),
            favouriteHotelRepository = get(),
            bookingRecordRepository = get(),
            destinationApiRepository = get(),
            authRepository = get()
        )
    }

    viewModelOf(::QuestionnaireViewModel)

    viewModel { (navController: NavHostController) ->
        RecommendationViewModel(
            navController = navController,
            userPreferencesRepository = get(),
            userProfileRepository = get(),
            destinationApiRepository = get()
        )
    }

    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        DestinationViewModel(
            navController = navController,
            sharedViewModel = sharedViewModel,
            userProfileRepository = get(),
            destinationApiRepository = get()
        )
    }
} 