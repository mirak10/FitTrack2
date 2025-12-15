package com.example.fittrackapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrackapp.data.database.entity.WorkoutLog
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    // Read all logs, ordered by date descending (newest first)
    @Query("SELECT * FROM workout_log ORDER BY startTime DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog)

    //  Delete a specific log entry by its ID
    @Query("DELETE FROM workout_log WHERE logId = :logId")
    suspend fun deleteLogById(logId: Int)


}