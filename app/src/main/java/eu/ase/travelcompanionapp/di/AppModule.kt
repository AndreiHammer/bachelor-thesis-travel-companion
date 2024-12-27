package eu.ase.travelcompanionapp.di

import eu.ase.travelcompanionapp.core.data.HttpClientFactory
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.AmadeusApiService
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.RemoteHotelDataSource
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.repository.AmadeusHotelRepository
import eu.ase.travelcompanionapp.hotel.data.placesApi.PlacesHotelRepository
import eu.ase.travelcompanionapp.hotel.data.placesApi.PlacesApiService
import eu.ase.travelcompanionapp.hotel.domain.HotelRepositoryAmadeusApi
import eu.ase.travelcompanionapp.hotel.domain.HotelRepositoryPlacesApi
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationViewModel
import eu.ase.travelcompanionapp.hotel.presentation.locationSearch.LocationSearchViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }

    viewModelOf(::HotelLocationViewModel)
    single<HotelRepositoryPlacesApi> { PlacesHotelRepository(get()) }
    single { PlacesApiService(get()) }

    viewModelOf(::HotelListViewModel)
    single<HotelRepositoryAmadeusApi> { AmadeusHotelRepository(get()) }
    single<RemoteHotelDataSource> { AmadeusApiService(get()) }

    viewModelOf(::SharedViewModel)

    viewModelOf(::LocationSearchViewModel)
}