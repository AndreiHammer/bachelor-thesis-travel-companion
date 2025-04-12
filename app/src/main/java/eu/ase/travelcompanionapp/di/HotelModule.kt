package eu.ase.travelcompanionapp.di

import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.hotel.data._IATAparsing.CityToIATACodeRepositoryImpl
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.AmadeusApiService
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.network.RemoteHotelDataSource
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.repository.AmadeusHotelRepository
import eu.ase.travelcompanionapp.hotel.data.placesApi.HotelThumbnailService
import eu.ase.travelcompanionapp.hotel.data.placesApi.PlacesApiService
import eu.ase.travelcompanionapp.hotel.data.placesApi.PlacesHotelRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.CityToIATACodeRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryAmadeusApi
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelRepositoryPlacesApi
import eu.ase.travelcompanionapp.hotel.domain.repository.HotelThumbnailRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.test.HotelLocationTestViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelList.HotelListViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.HotelOffersViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

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
            sharedViewModel = sharedViewModel,
            priceConverter = get(),
            hotelThumbnailRepository = get()
        )
    }
    single<HotelRepositoryAmadeusApi> { AmadeusHotelRepository(get()) }
    single<RemoteHotelDataSource> { AmadeusApiService(get()) }
    single<HotelThumbnailRepository> { HotelThumbnailService(get()) }
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
            priceConverter = get(),
            bookingService = get()
        )
    }
}