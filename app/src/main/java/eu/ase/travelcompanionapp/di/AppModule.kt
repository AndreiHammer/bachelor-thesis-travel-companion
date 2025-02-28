package eu.ase.travelcompanionapp.di

import eu.ase.travelcompanionapp.app.TravelCompanionApp
import eu.ase.travelcompanionapp.authentication.data.AccountService
import eu.ase.travelcompanionapp.authentication.domain.repository.AccountRepository
import eu.ase.travelcompanionapp.authentication.presentation.login.LoginViewModel
import eu.ase.travelcompanionapp.authentication.presentation.profile.ProfileViewModel
import eu.ase.travelcompanionapp.authentication.presentation.signUp.SignUpViewModel
import eu.ase.travelcompanionapp.authentication.presentation.splash.SplashViewModel
import eu.ase.travelcompanionapp.core.data.HttpClientFactory
import eu.ase.travelcompanionapp.core.data.localDB.UserEntity
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.AmadeusApiService
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.RemoteHotelDataSource
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.repository.AmadeusHotelRepository
import eu.ase.travelcompanionapp.hotel.data._IATAparsing.CityToIATACodeRepositoryImpl
import eu.ase.travelcompanionapp.hotel.data.placesApi.PlacesHotelRepository
import eu.ase.travelcompanionapp.hotel.data.placesApi.PlacesApiService
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryPlacesApi
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.HotelOffersViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchViewModel
import io.ktor.client.engine.cio.CIO
import io.objectbox.BoxStore
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val coreModule = module {
    single { HttpClientFactory.create(CIO.create()) }
    single<BoxStore> { (androidApplication() as TravelCompanionApp).boxStore }
}

val databaseModule = module {
    single { get<BoxStore>().boxFor(UserEntity::class.java) }
}

val authModule = module {
    single<AccountRepository> { AccountService(get()) }
    viewModelOf(::SplashViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ProfileViewModel)
}

val hotelPlacesModule = module {
    viewModelOf(::HotelLocationViewModel)
    single<HotelRepositoryPlacesApi> { PlacesHotelRepository(get()) }
    single { PlacesApiService(get()) }
}

val hotelAmadeusModule = module {
    viewModelOf(::HotelListViewModel)
    single<HotelRepositoryAmadeusApi> { AmadeusHotelRepository(get()) }
    single<RemoteHotelDataSource> { AmadeusApiService(get()) }
}

val hotelSharedModule = module {
    viewModelOf(::SharedViewModel)
    viewModelOf(::LocationSearchViewModel)
    single<CityToIATACodeRepository> { CityToIATACodeRepositoryImpl(get()) }
    viewModelOf(::HotelOffersViewModel)
}