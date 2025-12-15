package com.example.fittrackapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.fittrackapp.data.database.entity.Exercise

@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onAdd: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("3") }
    var reps by remember { mutableStateOf("10") }

    // We will use 45 seconds as a default duration for the exercise
    val defaultExerciseTimeSeconds = 45
    // We will use 60 seconds as a default rest time
    val defaultRestTimeSeconds = 60

    val isFormValid = name.isNotBlank() && sets.toIntOrNull() != null && reps.toIntOrNull() != null

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Add New Exercise", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Sets Input
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it.filter { char -> char.isDigit() } },
                    label = { Text("Sets") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Reps Input
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it.filter { char -> char.isDigit() } },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                // NOTE: We omit the time/rest time input fields for now for simplicity.

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newExercise = Exercise(
                                workoutId = 0, // Placeholder ID, will be set by Repository
                                name = name.trim(),
                                sets = sets.toInt(),
                                reps = reps.toInt(),
                                time = defaultExerciseTimeSeconds,
                                restTime = defaultRestTimeSeconds
                            )
                            onAdd(newExercise)
                        },
                        enabled = isFormValid
                    ) {
                        Text("ADD")
                    }
                }
            }
        }
    }
}