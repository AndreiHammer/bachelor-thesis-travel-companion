package eu.ase.travelcompanionapp.auth.presentation.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import eu.ase.travelcompanionapp.core.presentation.UiText
import eu.ase.travelcompanionapp.user.domain.repository.AccountRepository

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    // Email, Password, Confirm Password
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    // Sign-up state
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    fun onSignUpClick(context: Context) {
        val currentEmail = email.value
        val currentPassword = password.value
        val currentConfirmPassword = confirmPassword.value

        // Validate input fields
        if (currentEmail.isBlank() || currentPassword.isBlank() || currentConfirmPassword.isBlank()) {
            _signUpState.update {
                SignUpState.Error(UiText.StringResourceId(R.string.all_fields_are_required))
            }
            return
        }

        if (currentPassword != currentConfirmPassword) {
            _signUpState.update {
                SignUpState.Error(UiText.StringResourceId(R.string.passwords_do_not_match))
            }
            return
        }

        viewModelScope.launch {
            _signUpState.update { SignUpState.Loading }

            try {
                val userId = authRepository.signUp(currentEmail, currentPassword)
                accountRepository.createUser(userId, currentEmail)
                _signUpState.update { SignUpState.Success }
            } catch (e: Exception) {
                val errorMessage = UiText.DynamicString(e.message ?: context.getString(R.string.error_unknown))
                _signUpState.update { SignUpState.Error(errorMessage) }
            }
        }
    }

    fun resetState() {
        _signUpState.update {
            SignUpState.Idle
        }
    }
}

sealed class SignUpState {
    data object Idle : SignUpState()
    data object Loading : SignUpState()
    data object Success : SignUpState()
    data class Error(val message: UiText) : SignUpState()
}