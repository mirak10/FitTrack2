package com.example.fittrackapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.data.database.entity.Exercise
import com.example.fittrackapp.data.database.entity.Workout
import com.example.fittrackapp.data.database.entity.WorkoutLog // NEW IMPORT
import com.example.fittrackapp.data.database.model.WorkoutWithExercises
import com.example.fittrackapp.data.WorkoutRepository
import com.example.fittrackapp.session.WorkoutSessionManager
import com.example.fittrackapp.session.WorkoutSessionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first // NEW IMPORT
import kotlinx.coroutines.launch

class WorkoutsViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    // --- Data Properties (Persistence) ---

    // Exposes all workouts for the list screen
    val allWorkouts: Flow<List<WorkoutWithExercises>> = repository.getAllWorkouts()

    // --- CRUD Operations ---

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    // Used by WorkoutDetailScreen to observe a single workout
    fun getWorkoutFlowById(workoutId: Int): Flow<WorkoutWithExercises> {
        return repository.getWorkoutWithExercises(workoutId)
    }

    // --- Workout Session Management ---

    // Manager to handle the active workout session state and logic
    private var sessionManager: WorkoutSessionManager? = null

    /**
     * Fetches the workout data, initializes the session manager, and starts the timer.
     */
    fun startNewSession(workoutId: Int) {
        // Must launch a coroutine to fetch the data from Room
        viewModelScope.launch {

            // 1. Fetch the full workout data model
            val workoutData = repository.getWorkoutWithExercises(workoutId).first()

            // 2. Instantiate and start the new manager
            sessionManager?.stopAndFinish()

            sessionManager = WorkoutSessionManager(
                workoutData = workoutData,
                scope = viewModelScope
            )

            // 3. CRITICAL LOGGING INTEGRATION: Set the callback to save the log when finished
            sessionManager?.onSessionFinished = { log: WorkoutLog ->
                viewModelScope.launch {
                    repository.saveWorkoutLog(log)
                    // The view will navigate away when the state changes to isFinished
                }
            }

            sessionManager?.start()
        }
    }

    /**
     * Exposes the session state to the WorkoutSessionScreen.
     */
    fun getSessionState(): StateFlow<WorkoutSessionState>? = sessionManager?.state

    /**
     * Controls for the active session.
     */
    fun pauseSession() = sessionManager?.pause()
    fun stopSession() = sessionManager?.stopAndFinish()
    fun skipSessionPhase() = sessionManager?.skip()

    fun saveWorkoutPlan(workoutName: String, exercises: List<Exercise>) {
        viewModelScope.launch {
            val workout = Workout(name = workoutName)
            repository.saveWorkoutPlan(workout, exercises)
        }
    }
}