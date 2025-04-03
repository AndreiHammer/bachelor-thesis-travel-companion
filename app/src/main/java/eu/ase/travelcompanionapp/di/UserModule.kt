package eu.ase.travelcompanionapp.di

import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.ui.ThemeManager
import eu.ase.travelcompanionapp.user.data.AccountService
import eu.ase.travelcompanionapp.user.data.currencyApi.network.CurrencyApiService
import eu.ase.travelcompanionapp.user.data.currencyApi.network.RemoteCurrencyDataSource
import eu.ase.travelcompanionapp.user.data.currencyApi.repository.CurrencyApiRepository
import eu.ase.travelcompanionapp.user.data.preferences.UserPreferences
import eu.ase.travelcompanionapp.user.domain.repository.AccountRepository
import eu.ase.travelcompanionapp.user.domain.repository.CurrencyRepository
import eu.ase.travelcompanionapp.user.domain.repository.UserPreferencesRepository
import eu.ase.travelcompanionapp.user.domain.service.PriceConverter
import eu.ase.travelcompanionapp.user.presentation.profile.ProfileViewModel
import eu.ase.travelcompanionapp.user.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val userModule = module {
    single<AccountRepository> { AccountService(get()) }
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
            themeManager = get(),
            navController = navController
        )
    }
}

val currencyModule = module {
    single<RemoteCurrencyDataSource> { CurrencyApiService(get()) }
    single<CurrencyRepository> { CurrencyApiRepository(get()) }
    single { PriceConverter(get(), get()) }
}

val themeModule = module {
    single { ThemeManager(userPreferencesRepository = get()) }
}