package com.example.fittrackapp

import android.app.Application
import com.example.fittrackapp.data.WorkoutRepository
import com.example.fittrackapp.data.database.FitTrackDatabase

class FitTrackApplication : Application() {

    // Lazy initialization of the database instance
    private val database by lazy { FitTrackDatabase.getDatabase(this) }

    // The Repository, which is the dependency we need to inject into ViewModels
    val repository by lazy {
        WorkoutRepository(
            workoutDao = database.workoutDao(),
            exerciseDao = database.exerciseDao(),
            logDao = database.logDao()
        )
    }
}