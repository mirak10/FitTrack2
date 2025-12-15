package com.example.fittrackapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_log")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true)
    val logId: Int = 0,

    // Foreign key to link to the workout that was performed
    val workoutId: Int,

    // The name of the workout, useful for display even if the original workout is deleted
    val workoutName: String,

    // Time tracking
    val startTime: Long = System.currentTimeMillis(), // Timestamp of when the session started
    val endTime: Long = 0,
    val durationSeconds: Int = 0
)