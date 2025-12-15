package com.example.fittrackapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fittrackapp.data.WorkoutRepository


/**
 * Custom factory to instantiate ViewModels that require custom parameters,
 * specifically the WorkoutRepository.
 * * This allows ViewModels to be lifecycle-aware and allows the UI to use the
 * standard Android approach: viewModel()
 */
class FitTrackViewModelFactory(
    private val repository: WorkoutRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutsViewModel::class.java)) {
            return WorkoutsViewModel(repository) as T
        }
        // Handle other ViewModels here if the app grows
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}