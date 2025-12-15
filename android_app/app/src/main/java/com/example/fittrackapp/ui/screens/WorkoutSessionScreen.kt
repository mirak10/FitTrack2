package com.example.fittrackapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fittrackapp.ui.utils.getWorkoutsViewModel
import com.example.fittrackapp.session.WorkoutSessionState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    onSessionEnd: () -> Unit // Callback to navigate back to the list/detail screen
) {
    val viewModel = getWorkoutsViewModel()

    // Get the StateFlow from the ViewModel and collect it as a Compose state
    val sessionStateFlow = viewModel.getSessionState()

    // Safety check: if the flow is null, navigate back (shouldn't happen if navigating correctly)
    if (sessionStateFlow == null) {
        onSessionEnd()
        return
    }

    // Collect the state
    val state by sessionStateFlow.collectAsState()

    // Handle session finish
    if (state.isFinished) {
        // Show a quick Snackbar or dialog before navigating back
        LaunchedEffect(Unit) {
            // Give a moment for the user to see the change, then navigate away
            delay(1500)
            onSessionEnd()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Active Session: ${state.currentExerciseName}") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Current Status / Title
            Text(
                text = if (state.isRestPhase) "REST!" else "Perform Exercise",
                style = MaterialTheme.typography.headlineMedium,
                color = if (state.isRestPhase) MaterialTheme.colorScheme.primary else Color.Black
            )

            // 2. Timer Display
            Text(
                text = formatTime(state.timeRemaining),
                style = MaterialTheme.typography.displayLarge
            )

            // 3. Exercise and Sets Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.currentExerciseName,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sets Remaining: ${state.setsRemaining}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // 4. Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip Button
                Button(
                    onClick = { viewModel.skipSessionPhase() },
                    enabled = state.isRunning || state.isRestPhase, // Can skip while running or resting
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("SKIP", color = Color.White)
                }
                // Pause/Resume Button
                Button(
                    onClick = {
                        if (state.isRunning) viewModel.pauseSession() else viewModel.startNewSession(0) // Note: Needs better handling for resume vs start
                    },
                    // Only enabled if not finished
                    enabled = !state.isFinished,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    if (state.isRunning) {
                        Icon(Icons.Filled.Pause, contentDescription = "Pause")
                        Spacer(Modifier.width(8.dp))
                        Text("PAUSE")
                    } else {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Resume")
                        Spacer(Modifier.width(8.dp))
                        Text("RESUME")
                    }
                }

                // Stop Button
                OutlinedButton(
                    onClick = { viewModel.stopSession() },
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = "Stop")
                    Spacer(Modifier.width(8.dp))
                    Text("STOP")
                }
            }
        }
    }
}

// Helper function to format time (MM:SS)
fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}