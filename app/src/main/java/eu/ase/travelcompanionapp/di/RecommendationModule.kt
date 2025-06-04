package eu.ase.travelcompanionapp.di

import eu.ase.travelcompanionapp.recommendation.data.preferences.RecommendationUserPreferences
import eu.ase.travelcompanionapp.recommendation.domain.repository.UserPreferencesRepository
import eu.ase.travelcompanionapp.recommendation.presentation.RecommendationViewModel
import eu.ase.travelcompanionapp.recommendation.presentation.questionnaire.QuestionnaireViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val recommendationModule = module {
    single<UserPreferencesRepository> { RecommendationUserPreferences(get()) }


    viewModelOf(::RecommendationViewModel)
    viewModelOf(::QuestionnaireViewModel)
} 