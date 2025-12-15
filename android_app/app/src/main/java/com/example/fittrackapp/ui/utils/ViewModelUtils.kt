package com.example.fittrackapp.ui.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittrackapp.FitTrackApplication
import com.example.fittrackapp.viewmodel.FitTrackViewModelFactory
import com.example.fittrackapp.viewmodel.WorkoutsViewModel

// Utility function to retrieve the Activity from the current Compose context
fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

// Utility function to get the ViewModel with our custom Factory
@Composable
fun getWorkoutsViewModel(): WorkoutsViewModel {
    val context = LocalContext.current
    val application = context.getActivity()?.application as FitTrackApplication

    return viewModel(
        factory = FitTrackViewModelFactory(application.repository)
    )
}