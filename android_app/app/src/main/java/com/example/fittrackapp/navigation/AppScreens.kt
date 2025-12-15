package com.example.fittrackapp.navigation

/**
 * Defines the navigation routes used in the application.
 */
object AppScreens {
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val HOME = "home"        // The main container screen
    const val PLAN_LIST = "plan_list" // New: List of plans
    const val SESSION = "session"  // New: Active workout session
    const val HISTORY = "history"  // New: Workout history logs

    fun sessionRoute(workoutId: Int) = "session/$workoutId"
}