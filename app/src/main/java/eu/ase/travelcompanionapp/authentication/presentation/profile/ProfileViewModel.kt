package eu.ase.travelcompanionapp.authentication.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.authentication.domain.model.User
import eu.ase.travelcompanionapp.authentication.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountRepository: AccountRepository
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
    data object Success : ProfileActionState()
    data class Error(val message: String) : ProfileActionState()
}