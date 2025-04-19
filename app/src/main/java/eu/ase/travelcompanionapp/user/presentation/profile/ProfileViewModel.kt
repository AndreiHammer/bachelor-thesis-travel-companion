package eu.ase.travelcompanionapp.user.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import eu.ase.travelcompanionapp.app.navigation.routes.AuthRoute
import eu.ase.travelcompanionapp.app.navigation.routes.HotelRoute
import eu.ase.travelcompanionapp.app.navigation.routes.PaymentRoute
import eu.ase.travelcompanionapp.app.navigation.routes.ProfileRoute
import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import eu.ase.travelcompanionapp.user.domain.model.User
import eu.ase.travelcompanionapp.user.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
    private val navController: NavHostController
) : ViewModel() {
    private val _userData = MutableStateFlow(User())
    val userData: StateFlow<User> = _userData.asStateFlow()

    private val _actionState = MutableStateFlow<ProfileActionState>(ProfileActionState.Idle)
    val actionState: StateFlow<ProfileActionState> = _actionState.asStateFlow()

    private val _showSignOutDialog = MutableStateFlow(false)
    val showSignOutDialog: StateFlow<Boolean> = _showSignOutDialog.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            accountRepository.currentUser.collect { user ->
                user?.let {
                    _userData.value = it
                }
            }
        }
    }

    fun updateUserData(updatedUser: User) {
        viewModelScope.launch {
            try {
                accountRepository.updateUserProfile(updatedUser)
                loadUserData()
                _actionState.value = ProfileActionState.Success
            } catch (e: Exception) {
                _actionState.value = ProfileActionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setShowSignOutDialog(show: Boolean) {
        _showSignOutDialog.value = show
    }

    fun setShowDeleteDialog(show: Boolean) {
        _showDeleteDialog.value = show
    }

    fun handleAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnBackClick -> {
                navController.popBackStack()
            }
            ProfileAction.OnSettingsClick -> {
                navController.navigate(ProfileRoute.Settings)
            }
            ProfileAction.OnSignOutClick -> {
                _showSignOutDialog.value = true
            }
            ProfileAction.ConfirmSignOut -> {
                viewModelScope.launch {
                    try {
                        authRepository.signOut()
                        _actionState.value = ProfileActionState.SignedOut
                    } catch (e: Exception) {
                        _actionState.value = ProfileActionState.Error(e.message ?: "Sign out failed")
                    }
                }
            }
            ProfileAction.OnDeleteAccountClick -> {
                _showDeleteDialog.value = true
            }
            ProfileAction.ConfirmDeleteAccount -> {
                viewModelScope.launch {
                    try {
                        authRepository.deleteAccount()
                        _actionState.value = ProfileActionState.AccountDeleted
                    } catch (e: Exception) {
                        _actionState.value = ProfileActionState.Error(e.message ?: "Account deletion failed")
                    }
                }
            }
            ProfileAction.OnViewBookingHistoryClick -> {
                navController.navigate(PaymentRoute.BookingHistory)
            }
        }
    }

    fun handleStateChange(state: ProfileActionState) {
        when (state) {
            is ProfileActionState.SignedOut, is ProfileActionState.AccountDeleted -> {
                navController.navigate(AuthRoute.AuthGraph) {
                    popUpTo(HotelRoute.HotelGraph) {
                        inclusive = true
                    }
                }
            }
            else -> Unit
        }
    }
}

sealed class ProfileActionState {
    data object Idle : ProfileActionState()
    data object SignedOut : ProfileActionState()
    data object AccountDeleted : ProfileActionState()
    data object Success : ProfileActionState()
    data class Error(val message: String) : ProfileActionState()
}