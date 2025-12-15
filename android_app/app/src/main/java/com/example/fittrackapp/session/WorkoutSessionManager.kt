package com.example.fittrackapp.session

import com.example.fittrackapp.data.database.entity.WorkoutLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.fittrackapp.data.database.model.WorkoutWithExercises

/**
 * Data class representing the current state of the workout session.
 * @param timeRemaining The time left for the current exercise or rest period (in seconds).
 * @param isRunning True if the timer is actively counting down.
 * @param isRestPhase True if the user is currently in a rest period.
 * @param currentExerciseName The name of the exercise currently being performed.
 * @param setsRemaining Sets remaining for the current exercise.
 */
data class WorkoutSessionState(
    val timeRemaining: Int = 0,
    val isRunning: Boolean = false,
    val isRestPhase: Boolean = false,
    val currentExerciseName: String = "Ready",
    val setsRemaining: Int = 0,
    val isFinished: Boolean = false
)

class WorkoutSessionManager(
    private val workoutData: WorkoutWithExercises,
    private val scope: CoroutineScope
) {
    private var sessionStartTime: Long = 0L
    // 1. Private Mutable State Flow (for internal changes)
    private val _state = MutableStateFlow(WorkoutSessionState())

    // 2. Public Read-only State Flow (to expose the state to the ViewModel/UI)
    val state: StateFlow<WorkoutSessionState> = _state.asStateFlow()

    // 3. Timer Job reference
    private var timerJob: Job? = null

    // ... (StateFlow and timerJob remain the same) ...

    // --- Session Tracking State ---
    private var currentExerciseIndex: Int = 0
    private var currentSet: Int = 1
    private var isCurrentlyResting: Boolean = false

    private val currentExercise
        get() = workoutData.exercises.getOrNull(currentExerciseIndex)

    // --- Control Functions ---

    fun start() {
        if (_state.value.isRunning || workoutData.exercises.isEmpty()) {
            _state.value = _state.value.copy(isFinished = true)
            return
        }

        // SET START TIME when the session truly begins
        if (sessionStartTime == 0L) {
            sessionStartTime = System.currentTimeMillis()
        }

        // Start with the first exercise
        startExercisePhase()
    }

    private fun startExercisePhase() {
        val exercise = currentExercise ?: run {
            stopAndFinish()
            return
        }

        // If all sets for this exercise are complete, move to the next exercise
        if (currentSet > exercise.sets) {
            nextExercise()
            return
        }

        val setsRemaining = exercise.sets - currentSet + 1

        _state.value = _state.value.copy(
            isRunning = true,
            isRestPhase = false,
            currentExerciseName = exercise.name,
            setsRemaining = setsRemaining,
            timeRemaining = exercise.time // Start the exercise timer
        )
        isCurrentlyResting = false
        startTimer(_state.value.timeRemaining) {
            // Timer finished, now transition to rest or next set
            startRestPhase()
        }
    }

    private fun startRestPhase() {
        val exercise = currentExercise ?: return

        // Skip rest if it's the last set or last exercise
        if (currentSet == exercise.sets && currentExerciseIndex == workoutData.exercises.lastIndex) {
            stopAndFinish()
            return
        }

        // Use a fixed rest time (e.g., 60 seconds) or the exercise's rest time if available
        val restTime = exercise.restTime ?: 60

        _state.value = _state.value.copy(
            isRunning = true,
            isRestPhase = true,
            currentExerciseName = "REST",
            timeRemaining = restTime
        )
        isCurrentlyResting = true
        startTimer(restTime) {
            // Rest finished, move to the next set or exercise
            currentSet++
            startExercisePhase()
        }
    }

    // New generic timer function
    private fun startTimer(duration: Int, onFinish: () -> Unit) {
        timerJob?.cancel()
        var remaining = duration
        _state.value = _state.value.copy(timeRemaining = remaining)

        timerJob = scope.launch {
            while (remaining > 0) {
                delay(1000)
                remaining--
                _state.value = _state.value.copy(timeRemaining = remaining)
            }
            onFinish()
        }
    }

    private fun nextExercise() {
        currentExerciseIndex++
        currentSet = 1 // Reset sets for the new exercise
        startExercisePhase()
    }

    // --- Existing Control Functions (Pause, Stop) ---

    fun pause() {
        if (!_state.value.isRunning) return
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun stopAndFinish() {
        timerJob?.cancel()

        // 1. Calculate the final duration
        val totalDuration = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt()

        // 2. Prepare the log data for the callback
        val logData = WorkoutLog(
            workoutId = workoutData.workout.workoutId,
            workoutName = workoutData.workout.name,
            startTime = sessionStartTime,
            endTime = System.currentTimeMillis(),
            durationSeconds = totalDuration
        )

        // 3. Update internal state
        _state.value = WorkoutSessionState(isFinished = true)

        // 4. Trigger callback with log data
        onSessionFinished(logData) // NEW CALLBACK CALL
    }
    var onSessionFinished: (WorkoutLog) -> Unit = {}

    fun skip() {
        // 1. Cancel the current timer job immediately
        timerJob?.cancel()

        if (_state.value.isFinished) {
            // Nothing to skip if the workout is finished
            return
        }

        if (isCurrentlyResting) {
            // If we are resting, skip the rest and move directly to the next set/exercise
            currentSet++
            startExercisePhase()
        } else {
            // If we are doing an exercise, finish the current set and move to the rest phase (if not the last set/exercise)
            // Note: The timer finished callback logic is slightly simplified here.
            // We call startRestPhase, which manages the check for the final set/exercise.
            startRestPhase()
        }
    }

}