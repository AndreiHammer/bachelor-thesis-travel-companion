package eu.ase.travelcompanionapp.di

import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.app.navigation.bottomNavigation.BottomNavigationViewModel
import eu.ase.travelcompanionapp.auth.data.AuthService
import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import eu.ase.travelcompanionapp.user.data.AccountService
import eu.ase.travelcompanionapp.user.domain.repository.AccountRepository
import eu.ase.travelcompanionapp.auth.presentation.login.LoginViewModel
import eu.ase.travelcompanionapp.user.presentation.profile.ProfileViewModel
import eu.ase.travelcompanionapp.auth.presentation.signup.SignUpViewModel
import eu.ase.travelcompanionapp.auth.presentation.splash.SplashViewModel
import eu.ase.travelcompanionapp.core.data.network.HttpClientFactory
import eu.ase.travelcompanionapp.core.data.localDB.DatabaseManager
import eu.ase.travelcompanionapp.core.data.localDB.UserEntity
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.AmadeusApiService
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.RemoteHotelDataSource
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.repository.AmadeusHotelRepository
import eu.ase.travelcompanionapp.hotel.data._IATAparsing.CityToIATACodeRepositoryImpl
import eu.ase.travelcompanionapp.hotel.data.database.FavouriteHotelRepositoryImpl
import eu.ase.travelcompanionapp.hotel.data.database.repository.RemoteFavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.data.placesApi.PlacesHotelRepository
import eu.ase.travelcompanionapp.hotel.data.placesApi.PlacesApiService
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryPlacesApi
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.test.HotelLocationTestViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.HotelFavouriteViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.HotelOffersViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchViewModel
import eu.ase.travelcompanionapp.user.data.currencyApi.network.CurrencyApiService
import eu.ase.travelcompanionapp.user.data.currencyApi.network.RemoteCurrencyDataSource
import eu.ase.travelcompanionapp.user.data.currencyApi.repository.CurrencyApiRepository
import eu.ase.travelcompanionapp.user.data.preferences.UserPreferences
import eu.ase.travelcompanionapp.user.domain.repository.CurrencyRepository
import eu.ase.travelcompanionapp.user.domain.repository.UserPreferencesRepository
import eu.ase.travelcompanionapp.user.domain.service.PriceConverter
import eu.ase.travelcompanionapp.user.presentation.settings.SettingsViewModel
import io.ktor.client.engine.cio.CIO
import io.objectbox.BoxStore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val coreModule = module {
    single { HttpClientFactory.create(CIO.create()) }
    single<BoxStore> { DatabaseManager.boxStore }
    viewModelOf(::BottomNavigationViewModel)
}

val databaseModule = module {
    single { get<BoxStore>().boxFor(UserEntity::class.java) }
    single { RemoteFavouriteHotelRepository() }
    single <FavouriteHotelRepository>{ FavouriteHotelRepositoryImpl(get(), get()) }

    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        HotelFavouriteViewModel(
            navController = navController,
            sharedViewModel = sharedViewModel,
            favouriteHotelRepository = get()
        )
    }
}

val authModule = module {
    single<AuthRepository> { AuthService() }
    viewModelOf(::SplashViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::LoginViewModel)
}

val userModule = module {
    single<AccountRepository> { AccountService(get(), get()) }
    single<UserPreferencesRepository> { UserPreferences(get()) }
    viewModel { (navController: NavHostController) ->
        ProfileViewModel(
            accountRepository = get(),
            authRepository = get(),
            navController = navController
        )
    }
    viewModel { (navController: NavHostController) ->
        SettingsViewModel(
            userPreferences = get(),
            navController = navController
        )
    }
}

val hotelPlacesModule = module {
    /*viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        HotelLocationViewModel(
            hotelRepository = get(),
            navController = navController,
            sharedViewModel = sharedViewModel,
            favouriteHotelRepository = get()
        )
    }*/
    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        HotelLocationTestViewModel(
            navController = navController,
            sharedViewModel = sharedViewModel,
            favouriteHotelRepository = get()
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
            navController = navController,
            priceConverter = get()
        )
    }
}

val currencyModule = module {
    single<RemoteCurrencyDataSource> { CurrencyApiService(get()) }
    single<CurrencyRepository> { CurrencyApiRepository(get()) }
    single { PriceConverter(get(), get()) }
}