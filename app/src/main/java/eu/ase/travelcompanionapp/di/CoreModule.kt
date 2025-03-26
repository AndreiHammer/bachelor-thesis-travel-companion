package eu.ase.travelcompanionapp.di

import eu.ase.travelcompanionapp.app.navigation.bottomNavigation.BottomNavigationViewModel
import eu.ase.travelcompanionapp.core.data.localDB.DatabaseManager
import eu.ase.travelcompanionapp.core.data.network.HttpClientFactory
import io.ktor.client.engine.cio.CIO
import io.objectbox.BoxStore
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val coreModule = module {
    single { HttpClientFactory.create(CIO.create()) }
    single<BoxStore> { DatabaseManager.boxStore }
    viewModelOf(::BottomNavigationViewModel)
}