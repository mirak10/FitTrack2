package com.example.fittrackapp.data.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.fittrackapp.data.database.entity.Workout
import com.example.fittrackapp.data.database.entity.Exercise

/**
 * A data class used to represent a Workout plan along with all its associated Exercises.
 * Room automatically populates the 'exercises' list using the defined foreign key relationship.
 */
data class WorkoutWithExercises(
    // 1. The parent entity (the Workout name/ID)
    @Embedded
    val workout: Workout,

    // 2. The children entities (the list of exercises)
    @Relation(
        parentColumn = "workoutId", // The primary key in the Workout entity
        entityColumn = "workoutId"  // The foreign key in the Exercise entity
    )
    val exercises: List<Exercise>
)