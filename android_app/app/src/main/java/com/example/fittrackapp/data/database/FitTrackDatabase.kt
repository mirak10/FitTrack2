package com.example.fittrackapp.data.database // <-- REF: Updated package name

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fittrackapp.data.database.dao.ExerciseDao
import com.example.fittrackapp.data.database.dao.LogDao
import com.example.fittrackapp.data.database.dao.WorkoutDao
import com.example.fittrackapp.data.database.entity.Workout
import com.example.fittrackapp.data.database.entity.Exercise
import com.example.fittrackapp.data.database.entity.WorkoutLog

// 1. Database Annotation: Lists all entities and sets the version
@Database(
    entities = [Workout::class, Exercise::class, WorkoutLog::class],
    version = 2,
    exportSchema = false
)
abstract class FitTrackDatabase : RoomDatabase() {

    // 2. Abstract DAO methods: Room generates the implementation
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun logDao(): LogDao

    // 3. Singleton Implementation (Companion Object)
    companion object {
        @Volatile
        private var INSTANCE: FitTrackDatabase? = null

        fun getDatabase(context: Context): FitTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitTrackDatabase::class.java,
                    "fittrackapp_database" // Database name
                )
                    // Fallback to destructive migration is simple for a project
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}