package eu.ase.travelcompanionapp.auth.presentation.splash

import androidx.lifecycle.ViewModel
import eu.ase.travelcompanionapp.auth.domain.AuthRepository

class SplashViewModel(
    private val authService: AuthRepository
)  :ViewModel(){

    fun onAppStart(
        onUserKnown: () -> Unit,
        onUserUnknown: () -> Unit
    ) {
        if (authService.hasUser()) onUserKnown() else onUserUnknown()
    }
}