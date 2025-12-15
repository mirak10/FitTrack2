package com.example.fittrackapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.fittrackapp.data.repository.AuthRepository
import com.example.fittrackapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    authRepository: AuthRepository,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Manually create ViewModel using the shared factory
    val viewModel: AuthViewModel = AuthViewModelFactory(authRepository)
    val uiState by viewModel.uiState.collectAsState()

    // Check for immediate success (user is signed in after successful sign up)
    LaunchedEffect(uiState.isUserLoggedIn) {
        if (uiState.isUserLoggedIn) {
            onSignUpSuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("FitTrack Sign Up") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- Email Input ---
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Password Input ---
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = { Text("Password (Min 6 chars)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Sign Up Button ---
            Button(
                onClick = viewModel::signUp,
                enabled = !uiState.isLoading && uiState.email.isNotBlank() && uiState.password.length >= 6,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("SIGN UP")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- Login Prompt ---
            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Log In")
            }

            // --- Error Message Display ---
            uiState.error?.let { errorMessage ->
                Spacer(modifier = Modifier.height(16.dp))
                Snackbar(
                    action = {
                        TextButton(onClick = viewModel::dismissError) { Text("DISMISS") }
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(errorMessage)
                }
            }
        }
    }
}