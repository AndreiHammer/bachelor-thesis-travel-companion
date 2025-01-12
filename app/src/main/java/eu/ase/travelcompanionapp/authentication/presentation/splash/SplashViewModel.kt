package eu.ase.travelcompanionapp.authentication.presentation.splash

import androidx.lifecycle.ViewModel
import eu.ase.travelcompanionapp.authentication.domain.repository.AccountRepository

class SplashViewModel(
    private val accountService: AccountRepository
)  :ViewModel(){

    fun onAppStart(
        onUserKnown: () -> Unit,
        onUserUnknown: () -> Unit
    ) {
        if (accountService.hasUser()) onUserKnown() else onUserUnknown()
    }
}