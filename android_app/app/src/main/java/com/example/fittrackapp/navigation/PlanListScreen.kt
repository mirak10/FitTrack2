package com.example.fittrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittrackapp.data.WorkoutRepository
import com.example.fittrackapp.data.database.model.WorkoutWithExercises
import com.example.fittrackapp.viewmodel.WorkoutsViewModel

@Composable
fun WorkoutViewModelFactory(repository: WorkoutRepository): WorkoutsViewModel {
    return viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return WorkoutsViewModel(repository) as T
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanListScreen(
    workoutRepository: WorkoutRepository,
    onStartSession: (Int) -> Unit,
    onNavigateToCreatePlan: () -> Unit // Placeholder for a future screen
) {
    // 1. Initialize ViewModel
    val viewModel: WorkoutsViewModel = WorkoutViewModelFactory(workoutRepository)

    // 2. Collect state
    val workoutPlans by viewModel.allWorkouts.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Plans") },
                actions = {
                    IconButton(onClick = onNavigateToCreatePlan) {
                        Icon(Icons.Filled.Add, contentDescription = "Add New Plan")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            if (workoutPlans.isEmpty()) {
                item {
                    EmptyState(onNavigateToCreatePlan)
                }
            } else {
                items(workoutPlans, key = { it.workout.workoutId }) { plan ->
                    WorkoutPlanItem(
                        plan = plan,
                        onStartSession = onStartSession,
                        onDelete = viewModel::deleteWorkout
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutPlanItem(
    plan: WorkoutWithExercises,
    onStartSession: (Int) -> Unit,
    onDelete: (com.example.fittrackapp.data.database.entity.Workout) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Content: Name and details
            Column(Modifier.weight(1f)) {
                Text(
                    text = plan.workout.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${plan.exercises.size} Exercises",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right Actions: Start and Delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Start Session Button
                IconButton(onClick = { onStartSession(plan.workout.workoutId) }) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = "Start Workout",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Delete Button
                IconButton(onClick = { onDelete(plan.workout) }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete Workout",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(onNavigateToCreatePlan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No workout plans found.",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tap the plus icon to create your first plan!",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNavigateToCreatePlan) {
            Text("Create Plan")
        }
    }
}