package com.example.fittrackapp

import android.app.Application
import com.example.fittrackapp.data.WorkoutRepository
import com.example.fittrackapp.data.database.FitTrackDatabase

class FitTrackApplication : Application() {

    private val database by lazy { FitTrackDatabase.getDatabase(this) }

    val repository by lazy {
        WorkoutRepository(
            workoutDao = database.workoutDao(),
            exerciseDao = database.exerciseDao(),
            logDao = database.logDao()
        )
    }
}