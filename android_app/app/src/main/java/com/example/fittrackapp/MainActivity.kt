package com.example.fittrackapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.fittrackapp.data.repository.AuthRepository
import com.example.fittrackapp.ui.screens.* 
import com.example.fittrackapp.ui.theme.FitTrackAppTheme
import com.google.firebase.auth.FirebaseAuth

/**
 * Enum class to manage all screens/states of the application.
 */
enum class Screen {
    LOGIN,          // New: Authentication screen
    SIGN_UP,        // New: Registration screen
    WORKOUTS_LIST,
    CREATE_WORKOUT,
    WORKOUT_DETAIL,
    TIMER_SESSION
}

class MainActivity : ComponentActivity() {

    // 1. Instantiate Auth Repository using lazy delegate for safety
    private val authRepository by lazy { AuthRepository(FirebaseAuth.getInstance()) }

    // 2. Determine the initial screen based on Firebase status
    private val initialStartScreen: Screen by lazy {
        if (authRepository.currentUser.value != null) {
            Screen.WORKOUTS_LIST
        } else {
            Screen.LOGIN
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitTrackAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // State to track the current screen, initialized with the Firebase check
                    var currentScreen by remember { mutableStateOf(initialStartScreen) }

                    // State to hold the ID of the workout
                    var selectedWorkoutId by remember { mutableStateOf(-1) }

                    when (currentScreen) {
                        // --- Authentication Flow ---
                        Screen.LOGIN -> LoginScreen(
                            authRepository = authRepository, // Pass the dependency
                            onLoginSuccess = { currentScreen = Screen.WORKOUTS_LIST },
                            onNavigateToSignUp = { currentScreen = Screen.SIGN_UP }
                        )
                        Screen.SIGN_UP -> SignUpScreen(
                            authRepository = authRepository, // Pass the dependency
                            onSignUpSuccess = { currentScreen = Screen.WORKOUTS_LIST },
                            onNavigateToLogin = { currentScreen = Screen.LOGIN }
                        )

                        // --- Authenticated App Flow ---
                        Screen.WORKOUTS_LIST -> WorkoutsListScreen(
                            // to properly navigate back to LOGIN state.
                            onNavigateToCreate = { currentScreen = Screen.CREATE_WORKOUT },
                            onNavigateToDetail = { id ->
                                selectedWorkoutId = id
                                currentScreen = Screen.WORKOUT_DETAIL
                            }
                        )
                        Screen.CREATE_WORKOUT -> CreateWorkoutScreen(
                            onSaveSuccess = { currentScreen = Screen.WORKOUTS_LIST }
                        )
                        Screen.WORKOUT_DETAIL -> WorkoutDetailScreen(
                            workoutId = selectedWorkoutId,
                            onNavigateBack = { currentScreen = Screen.WORKOUTS_LIST },
                            onStartWorkout = {
                                currentScreen = Screen.TIMER_SESSION
                            }
                        )
                        Screen.TIMER_SESSION -> WorkoutSessionScreen(
                            onSessionEnd = { currentScreen = Screen.WORKOUTS_LIST }
                        )
                    }
                }
            }
        }
    }
}
