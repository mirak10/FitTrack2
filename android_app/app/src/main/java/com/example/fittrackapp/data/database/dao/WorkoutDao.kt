package com.example.fittrackapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Transaction // Required for relationship queries
import com.example.fittrackapp.data.database.entity.Workout
import com.example.fittrackapp.data.database.entity.WorkoutLog
import com.example.fittrackapp.data.database.model.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // --- BASIC CRUD OPERATIONS (Individual Entity) ---

    // R: Read all simple workout entities (for a quick list if needed)
    @Query("SELECT * FROM workout ORDER BY workoutId ASC")
    fun getAllWorkouts(): Flow<List<Workout>>

    // C: Create a new workout plan (returns the new primary key ID)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    // R: Read a single workout entity by ID
    @Query("SELECT * FROM workout WHERE workoutId = :id")
    suspend fun getWorkoutById(id: Int): Workout?

    // D: Delete a specific workout. Due to ForeignKey.CASCADE in Exercise.kt,
    // all linked exercises will also be deleted automatically.
    @Delete
    suspend fun deleteWorkout(workout: Workout)



    //Read all Workout plans along with the list of Exercises for each.
        // @Transaction is needed because this query involves multiple tables/steps.
    @Transaction
    @Query("SELECT * FROM workout ORDER BY workoutId ASC")
    fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>>

    //Read a single Workout plan along with its Exercises by ID.
    @Transaction
    @Query("SELECT * FROM workout WHERE workoutId = :workoutId")
    fun getWorkoutWithExercises(workoutId: Int): Flow<WorkoutWithExercises>

}