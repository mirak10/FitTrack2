package com.example.fittrackapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fittrackapp.data.database.model.WorkoutWithExercises
import com.example.fittrackapp.ui.utils.getWorkoutsViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.PlayArrow
// No longer needed since we are using the Button inside WorkoutDetailContent:
// import androidx.compose.material3.ExtendedFloatingActionButton
// import androidx.compose.material3.Icon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: Int,
    onNavigateBack: () -> Unit,
    onStartWorkout: (Int) -> Unit // The callback to MainActivity to change the screen
) {
    val viewModel = getWorkoutsViewModel()

    // 1. Observe the Flow for the single, specific workout
    val workoutFlow = viewModel.getWorkoutFlowById(workoutId)
    val workoutWithExercises by workoutFlow.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workoutWithExercises?.workout?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
        // REMOVED: floatingActionButton as the button is part of the content now
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Check if the data has loaded
            workoutWithExercises?.let { workoutData ->
                WorkoutDetailContent(
                    workoutData = workoutData,
                    onStartWorkout = {
                        // 1. Start the session setup in the ViewModel
                        viewModel.startNewSession(workoutId)

                        // 2. Navigate to the new Timer screen via MainActivity callback
                        onStartWorkout(workoutId)
                    }
                )
            } ?: run {
                // Loading state or workout not found
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun WorkoutDetailContent(
    workoutData: WorkoutWithExercises,
    onStartWorkout: () -> Unit // Now triggers the ViewModel and navigation
) {
    // 1. Start Workout Button
    Button(
        onClick = onStartWorkout, // *** UPDATED ONCLICK ACTION ***
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(20.dp)
    ) {
        // Add an icon for visual interest
        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text("START WORKOUT", style = MaterialTheme.typography.titleLarge)
    }

    // 2. List of Exercises
    Text(
        "Exercises in Plan",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workoutData.exercises, key = { it.name }) { exercise ->
            ExerciseDetailCard(exercise.name, exercise.sets, exercise.reps)
        }
    }
}

// ... (ExerciseDetailCard remains the same)

@Composable
fun ExerciseDetailCard(name: String, sets: Int, reps: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "$sets x $reps",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}