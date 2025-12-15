package com.example.fittrackapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class to define all routes and metadata for screens in the application.
 * This will be used by the Navigation Component.
 */
sealed class AppDestination(val route: String, val icon: ImageVector?, val label: String) {

    // --- Bottom Navigation Tabs ---
    object Workouts : AppDestination("workouts_list_route", Icons.Filled.Home, "Workouts")
    object Logs : AppDestination("logs_route", Icons.Filled.DateRange, "Logs")
    object Settings : AppDestination("settings_route", Icons.Filled.Settings, "Settings")

    // --- Screens accessible from within a tab (not in the Bottom Nav) ---
    object CreateWorkout : AppDestination("create_workout_route", Icons.Filled.Add, "Create")
    object WorkoutDetail : AppDestination("workout_detail_route/{id}", Icons.Filled.Search, "Detail")

    companion object {
        // List of destinations that appear in the Bottom Navigation Bar
        val bottomNavDestinations = listOf(Workouts, Logs, Settings)
    }
}