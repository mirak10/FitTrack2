package com.example.fittrackapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

// Defines the one-to-many relationship: One Workout has many Exercises
@Entity(
    tableName = "exercise",
    foreignKeys = [ForeignKey(
        entity = Workout::class,
        parentColumns = ["workoutId"], // Column in the Workout table
        childColumns = ["workoutId"],  // Column in this Exercise table
        onDelete = ForeignKey.CASCADE  // If the Workout is deleted, delete all related Exercises
    )],
            indices = [Index(value = ["workoutId"])]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val exerciseId: Int = 0,
    val workoutId: Int, // The ID of the parent Workout plan
    val name: String,     // e.g., "Push ups"
    val sets: Int,        // e.g., 3
    val reps: Int,         // e.g., 10

    val time: Int, // The duration of the exercise in seconds (e.g., 60 seconds)
    val restTime: Int = 60 // The rest time in seconds after this set/exercise, defaulting to 60
)