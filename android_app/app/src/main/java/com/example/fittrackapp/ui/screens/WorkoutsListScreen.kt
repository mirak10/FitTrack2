package com.example.fittrackapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fittrackapp.data.database.model.WorkoutWithExercises
import com.example.fittrackapp.ui.utils.getWorkoutsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Int) -> Unit // To view/start a specific workout
) {
    val viewModel = getWorkoutsViewModel()
    // Observe the Flow of all workouts from the ViewModel
    val allWorkouts by viewModel.allWorkouts.collectAsState(initial = emptyList())


    Scaffold(
        topBar = { TopAppBar(title = { Text("Your Workouts") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Workout")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            if (allWorkouts.isEmpty()) {
                // Display message when no workouts exist
                Text(
                    text = "Press '+' to create your first workout plan!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allWorkouts, key = { it.workout.workoutId }) { workoutWithExercises ->
                        WorkoutListItem(
                            workout = workoutWithExercises,
                            onClick = { onNavigateToDetail(workoutWithExercises.workout.workoutId) },
                            onDelete = { viewModel.deleteWorkout(workoutWithExercises.workout) } // Deleting the main workout
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutListItem(
    workout: WorkoutWithExercises,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(workout.workout.name, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "${workout.exercises.size} Exercises",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // For now, let's include a simple delete button for testing the CRUD logic
            TextButton(onClick = onDelete) {
                Text("DELETE")
            }
        }
    }
}