package com.example.fittrackapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log")
data class Log(
    @PrimaryKey(autoGenerate = true)
    val logId: Int = 0,
    val workoutId: Int, // Links to the Workout plan that was executed
    val date: Long,     // Timestamp of when the workout was completed (e.g., System.currentTimeMillis())
    val duration: Long  // Time spent on the workout (in milliseconds)
)