package com.example.fittrackapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost // NEW IMPORT
import androidx.navigation.compose.composable // NEW IMPORT
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController // NEW IMPORT
import androidx.navigation.navArgument
import com.example.fittrackapp.ui.AppDestination
import com.example.fittrackapp.ui.utils.getWorkoutsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // 1. Setup Navigation Controller
    val navController = rememberNavController()
    // Helper to get the current route for highlighting the Bottom Nav item
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: AppDestination.Workouts.route

    // The ViewModel is hoisted here so all screens can access it if needed
    val viewModel = getWorkoutsViewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestination.bottomNavDestinations.forEach { destination ->
                    NavigationBarItem(
                        // Check if the current route starts with the destination route for deep links
                        selected = currentRoute.startsWith(destination.route),
                        onClick = {
                            // Pop up to the start destination of the graph to avoid building up a large stack
                            // of destinations on the back stack as users select items
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = { destination.icon?.let { Icon(it, contentDescription = destination.label) } },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { paddingValues ->

        // 2. The Navigation Host defines the possible screens
        NavHost(
            navController = navController,
            startDestination = AppDestination.Workouts.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // A. Workouts Tab (The primary screen)
            composable(AppDestination.Workouts.route) {
                WorkoutsListScreen(
                    onNavigateToCreate = { navController.navigate(AppDestination.CreateWorkout.route) },
                    onNavigateToDetail = { id ->
                        navController.navigate("workout_detail_route/$id")
                    }
                )
            }

            // B. Create Workout Screen
            composable(AppDestination.CreateWorkout.route) {
                CreateWorkoutScreen(
                    onSaveSuccess = { navController.popBackStack() } // Go back to Workouts List
                )
            }

            // C. Workout Detail Screen (Requires an ID argument)
            composable(
                route = AppDestination.WorkoutDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getInt("id") ?: 0
                WorkoutDetailScreen(
                    workoutId = workoutId,
                    onNavigateBack = { navController.popBackStack() },
                    onStartWorkout = {
                        // To be implemented: navController.navigate("timer_session_route/$it")
                    }
                )
            }

            // D. Logs Tab (Placeholder)
            composable(AppDestination.Logs.route) {
                Text("Logs Content Goes Here", modifier = Modifier.padding(16.dp))
            }

            // E. Settings Tab (Placeholder)
            composable(AppDestination.Settings.route) {
                Text("Settings Content Goes Here", modifier = Modifier.padding(16.dp))
            }
        }
    }
}