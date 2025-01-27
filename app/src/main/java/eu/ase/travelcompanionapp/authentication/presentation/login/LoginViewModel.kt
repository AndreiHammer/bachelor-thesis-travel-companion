package eu.ase.travelcompanionapp.authentication.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.authentication.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import eu.ase.travelcompanionapp.core.presentation.UiText

class LoginViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    // Login state
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun onLoginClick(context: Context) {
        val currentEmail = email.value
        val currentPassword = password.value

        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _loginState.update {
                LoginState.Error(UiText.StringResourceId(R.string.email_and_password_are_required))
            }
            return
        }

        viewModelScope.launch {
            _loginState.update { LoginState.Loading }

            try {
                accountRepository.signIn(currentEmail, currentPassword)
                _loginState.update { LoginState.Success }
            } catch (e: Exception) {
                val errorMessage = UiText.DynamicString(e.message ?: context.getString(R.string.error_unknown))
                _loginState.update { LoginState.Error(errorMessage) }
            }
        }
    }

    fun resetState() {
        _loginState.update { LoginState.Idle }
    }
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: UiText) : LoginState()
}