package com.example.fittrackapp.data

import com.example.fittrackapp.data.database.dao.ExerciseDao
import com.example.fittrackapp.data.database.dao.LogDao
import com.example.fittrackapp.data.database.dao.WorkoutDao
import com.example.fittrackapp.data.database.entity.Exercise
import com.example.fittrackapp.data.database.entity.Workout
import com.example.fittrackapp.data.database.entity.WorkoutLog
import com.example.fittrackapp.data.database.model.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

// The Repository takes the DAOs as dependencies (Dependency Injection)
class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val logDao: LogDao
) {

    // --- WORKOUT & EXERCISE OPERATIONS ---

    fun getAllWorkouts(): Flow<List<WorkoutWithExercises>> {
        return workoutDao.getAllWorkoutsWithExercises()
    }

    fun getWorkoutWithExercises(workoutId: Int): Flow<WorkoutWithExercises> {
        return workoutDao.getWorkoutWithExercises(workoutId)
    }

    /**
     * C/U: Standardized function to save a new workout plan or update an existing one.
     * This function replaces createNewWorkoutPlan and the old saveWorkoutWithExercises.
     */
    suspend fun saveWorkoutPlan(workout: Workout, exercises: List<Exercise>) {
        // 1. Insert/Replace the main Workout (returns the new/existing ID)
        val workoutId = workoutDao.insertWorkout(workout)

        // 2. Map the list of Exercises to include the new/updated workoutId
        exercises.map {
            it.copy(workoutId = workoutId.toInt())
        }.also { exercisesWithId ->
            // 3. Insert all the exercises using the Exercise DAO
            exerciseDao.insertAll(exercisesWithId)
        }
    }

    // D: Delete a workout (Room CASCADE handles deleting linked exercises and logs)
    suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout)
    }


    // --- LOG OPERATIONS ---

    /**
     * C: Creates a new log entry after a workout session has finished.
     */
    suspend fun saveWorkoutLog(log: WorkoutLog) {
        logDao.insertWorkoutLog(log)
    }

    // R: Add the function to read all logs for the Logs Screen
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>> {
        return logDao.getAllWorkoutLogs()
    }


}