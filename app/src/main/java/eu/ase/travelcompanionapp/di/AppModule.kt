package eu.ase.travelcompanionapp.di

import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.authentication.data.AccountService
import eu.ase.travelcompanionapp.authentication.domain.repository.AccountRepository
import eu.ase.travelcompanionapp.authentication.presentation.login.LoginViewModel
import eu.ase.travelcompanionapp.authentication.presentation.profile.ProfileViewModel
import eu.ase.travelcompanionapp.authentication.presentation.signUp.SignUpViewModel
import eu.ase.travelcompanionapp.authentication.presentation.splash.SplashViewModel
import eu.ase.travelcompanionapp.core.data.network.HttpClientFactory
import eu.ase.travelcompanionapp.core.data.localDB.DatabaseManager
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
import eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.HotelFavouriteViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.HotelOffersViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchViewModel
import io.ktor.client.engine.cio.CIO
import io.objectbox.BoxStore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val coreModule = module {
    single { HttpClientFactory.create(CIO.create()) }
    single<BoxStore> { DatabaseManager.boxStore }
}

val databaseModule = module {
    single { get<BoxStore>().boxFor(UserEntity::class.java) }
    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        HotelFavouriteViewModel(
            navController = navController,
            sharedViewModel = sharedViewModel
        )
    }
}

val authModule = module {
    single<AccountRepository> { AccountService(get()) }
    viewModelOf(::SplashViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::LoginViewModel)
    viewModel { (navController: NavHostController) ->
        ProfileViewModel(
            accountRepository = get(),
            navController = navController
        )
    }
}

val hotelPlacesModule = module {
    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        HotelLocationViewModel(
            hotelRepository = get(),
            navController = navController,
            sharedViewModel = sharedViewModel
        )
    }
    single<HotelRepositoryPlacesApi> { PlacesHotelRepository(get()) }
    single { PlacesApiService(get()) }
}

val hotelAmadeusModule = module {
    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        HotelListViewModel(
            hotelRepository = get(),
            cityToIATACodeRepository = get(),
            navController = navController,
            sharedViewModel = sharedViewModel
        )
    }
    single<HotelRepositoryAmadeusApi> { AmadeusHotelRepository(get()) }
    single<RemoteHotelDataSource> { AmadeusApiService(get()) }
}

val hotelSharedModule = module {
    single<CityToIATACodeRepository> { CityToIATACodeRepositoryImpl(get()) }
    viewModelOf(::SharedViewModel)
    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        LocationSearchViewModel(
            cityToIATACodeRepository = get(),
            navController = navController,
            sharedViewModel = sharedViewModel
        )
    }
    viewModel { (navController: NavHostController) ->
        HotelOffersViewModel(
            hotelRepositoryAmadeusApi = get(),
            navController = navController
        )
    }
}