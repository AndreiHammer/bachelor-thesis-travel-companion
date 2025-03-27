package eu.ase.travelcompanionapp.di

import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.core.data.localDB.UserEntity
import eu.ase.travelcompanionapp.hotel.data.database.FavouriteHotelRepositoryImpl
import eu.ase.travelcompanionapp.hotel.data.database.repository.RemoteFavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.domain.repository.FavouriteHotelRepository
import eu.ase.travelcompanionapp.hotel.presentation.SharedViewModel
import eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.HotelFavouriteViewModel
import io.objectbox.BoxStore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single { get<BoxStore>().boxFor(UserEntity::class.java) }
    single { RemoteFavouriteHotelRepository() }
    single <FavouriteHotelRepository>{ FavouriteHotelRepositoryImpl(get(), get()) }

    viewModel { (navController: NavHostController, sharedViewModel: SharedViewModel) ->
        HotelFavouriteViewModel(
            navController = navController,
            sharedViewModel = sharedViewModel,
            favouriteHotelRepository = get(),
            priceConverter = get(),
            hotelRepository = get()
        )
    }
}