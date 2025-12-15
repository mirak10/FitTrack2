package com.example.fittrackapp.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Singleton repository responsible for all Firebase Authentication operations.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // --- 1. State Management ---

    // Private MutableStateFlow to hold the current authentication state.
    // It is initialized with the current user object, which will be null if nobody is logged in.
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)

    // Public immutable StateFlow for the rest of the application to observe.
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        // Listen for authentication state changes and update the StateFlow in real-time.
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    // --- 2. Authentication Operations ---

    /**
     * Attempts to register a new user with email and password.
     * @return AuthResult on success. Throws Exception on failure.
     */
    suspend fun signUp(email: String, password: String): AuthResult {
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    /**
     * Attempts to sign in an existing user with email and password.
     * @return AuthResult on success. Throws Exception on failure.
     */
    suspend fun signIn(email: String, password: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, password).await()
    }

    /**
     * Signs out the currently authenticated user.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Sends a password reset email to the given address.
     */
    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}