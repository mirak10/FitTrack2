package com.example.fittrackapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrackapp.data.database.entity.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    // ead all exercises for a specific workout ID
    @Query("SELECT * FROM exercise WHERE workoutId = :workoutId ORDER BY exerciseId ASC")
    fun getExercisesForWorkout(workoutId: Int): Flow<List<Exercise>>

    //Insert/Update one or more exercises. REPLACE strategy allows for updates.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    // Insert/Update a single exercise.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)

    //Delete a specific exercise by its ID
    @Query("DELETE FROM exercise WHERE exerciseId = :exerciseId")
    suspend fun deleteExerciseById(exerciseId: Int)
}