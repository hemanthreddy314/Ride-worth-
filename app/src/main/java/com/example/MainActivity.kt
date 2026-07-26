package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.AppNavGraph
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.HomeViewModel
import android.util.Log
import com.example.ui.screens.error.ErrorInitializationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            try {
                val app = RideWorthApplication.getInstance()
                
                // Check for initialization errors
                if (app.hasInitializationError()) {
                    Log.e("MainActivity", "App initialization error detected", app.getInitializationError())
                    ErrorInitializationScreen(
                        error = app.getInitializationError()?.message ?: "Unknown error"
                    )
                    return@setContent
                }

                val database = app.getDatabase()
                val valuationRepository = app.getRepository()

                if (database == null || valuationRepository == null) {
                    Log.e("MainActivity", "Database or Repository is null")
                    ErrorInitializationScreen(
                        error = "Failed to initialize database or repository"
                    )
                    return@setContent
                }

                val homeViewModel: HomeViewModel = viewModel()
                val historyViewModel: HistoryViewModel = viewModel {
                    HistoryViewModel(valuationRepository)
                }

                AppNavGraph(
                    homeViewModel = homeViewModel,
                    historyViewModel = historyViewModel
                )
            } catch (e: Exception) {
                Log.e("MainActivity", "Error initializing app: ${e.message}", e)
                ErrorInitializationScreen(
                    error = "Application failed to initialize: ${e.message}"
                )
            }
        }
    }
}
