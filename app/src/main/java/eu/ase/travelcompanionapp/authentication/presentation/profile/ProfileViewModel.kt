package eu.ase.travelcompanionapp.authentication.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.authentication.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _actionState = MutableStateFlow<ProfileActionState>(ProfileActionState.Idle)
    val actionState: StateFlow<ProfileActionState> = _actionState.asStateFlow()

    init {
        // Fetch the current user's email
        viewModelScope.launch {
            accountRepository.currentUser.collect { user ->
                _userEmail.value = user?.email
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                accountRepository.signOut()
                _actionState.value = ProfileActionState.SignedOut
            } catch (e: Exception) {
                _actionState.value = ProfileActionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                accountRepository.deleteAccount()
                _actionState.value = ProfileActionState.AccountDeleted
            } catch (e: Exception) {
                _actionState.value = ProfileActionState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class ProfileActionState {
    data object Idle : ProfileActionState()
    data object SignedOut : ProfileActionState()
    data object AccountDeleted : ProfileActionState()
    data class Error(val message: String) : ProfileActionState()
}