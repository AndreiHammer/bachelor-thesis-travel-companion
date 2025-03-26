package eu.ase.travelcompanionapp.di

import eu.ase.travelcompanionapp.auth.data.AuthService
import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import eu.ase.travelcompanionapp.auth.presentation.login.LoginViewModel
import eu.ase.travelcompanionapp.auth.presentation.signup.SignUpViewModel
import eu.ase.travelcompanionapp.auth.presentation.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { AuthService() }
    viewModelOf(::SplashViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::LoginViewModel)
}