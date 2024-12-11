package eu.ase.travelcompanionapp.di

import eu.ase.travelcompanionapp.core.data.HttpClientFactory
import eu.ase.travelcompanionapp.hotel.data.placesApi.HotelRepositoryImpl
import eu.ase.travelcompanionapp.hotel.data.placesApi.PlacesApiService
import eu.ase.travelcompanionapp.hotel.domain.HotelRepositoryPlacesApi
import eu.ase.travelcompanionapp.hotel.presentation.location.HotelLocationViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }

    viewModelOf(::HotelLocationViewModel)
    single<HotelRepositoryPlacesApi> { HotelRepositoryImpl(get()) }
    single { PlacesApiService(get()) }
}