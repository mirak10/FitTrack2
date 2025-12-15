package com.example.fittrackapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fittrackapp.data.database.entity.Exercise
import com.example.fittrackapp.ui.utils.getWorkoutsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(onSaveSuccess: () -> Unit) {
    val viewModel = getWorkoutsViewModel()

    // Local state for the new workout plan
    var workoutName by remember { mutableStateOf("") }
    val exerciseList = remember { mutableStateListOf<Exercise>() }

    // State to control the visibility of the Add Exercise dialog
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Workout") },
                actions = {
                    // Save Button
                    Button(
                        onClick = {
                            if (workoutName.isNotBlank() && exerciseList.isNotEmpty()) {
                                viewModel.saveWorkoutPlan(workoutName, exerciseList.toList())
                                onSaveSuccess() // Navigate back or show a confirmation
                            }
                        },
                        enabled = workoutName.isNotBlank() && exerciseList.isNotEmpty()
                    ) {
                        Text("SAVE")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExerciseDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Exercise")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 1. Workout Name Input
            OutlinedTextField(
                value = workoutName,
                onValueChange = { workoutName = it },
                label = { Text("Workout Name (e.g., Monday)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Exercise List Display
            Text("Exercises (${exerciseList.size})", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                items(exerciseList, key = { it.name + it.sets + it.reps }) { exercise ->
                    ExerciseListItem(
                        exercise = exercise,
                        onDelete = { exerciseList.remove(exercise) }
                    )
                }
            }
        }
    }

    // 3. Dialog for Adding Exercises
    if (showAddExerciseDialog) {
        AddExerciseDialog(
            onDismiss = { showAddExerciseDialog = false },
            onAdd = { newExercise ->
                exerciseList.add(newExercise)
                showAddExerciseDialog = false
            }
        )
    }
}

@Composable
fun ExerciseListItem(exercise: Exercise, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                Text("${exercise.sets} sets of ${exercise.reps} reps", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}