package com.example.fittrackapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class representing the mutable state of the Authentication screen.
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUserLoggedIn: Boolean = false // Derived from AuthRepository
)

/**
 * ViewModel responsible for handling user authentication logic and state management.
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Mutable state for the UI screen
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Collect the real-time user login status from the repository
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { currentState ->
                    currentState.copy(isUserLoggedIn = user != null)
                }
            }
        }
    }

    // --- User Input Handlers ---

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    // --- Core Authentication Methods ---

    fun signIn() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                authRepository.signIn(
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )
                // If successful, the authRepository's StateFlow updates,
                // which then updates our isUserLoggedIn state via the init block.

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthException -> "Login Failed: ${e.message ?: "Invalid credentials"}"
                    else -> "An unexpected error occurred."
                }
                _uiState.update { it.copy(error = errorMessage) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun signUp() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                authRepository.signUp(
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )
                // New user registered and automatically signed in.

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthException -> "Sign Up Failed: ${e.message ?: "Invalid email format or password too short."}"
                    else -> "An unexpected error occurred."
                }
                _uiState.update { it.copy(error = errorMessage) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}