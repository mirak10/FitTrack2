package com.example.fittrackapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val workoutId: Int = 0, // Unique identifier for the workout plan
    val name: String        // The name given by the user (e.g., "Monday")
)